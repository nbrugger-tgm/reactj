package com.niton.reactj;

import com.niton.reactj.annotation.ReactivResolution;
import com.niton.reactj.annotation.Reactive;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ReactiveController<C> {

	private final ReactiveComponent<C> view;
	private ReactiveObject model;
	private final Map<String,Object> valueCache = new HashMap<>();
	private final Map<String, List<ReactiveBinder.Binding<?>>> displayFunctions = new HashMap<>();
	private final Map<String, ReactiveBinder.ValueReceiver<?>> valueReceivers = new HashMap<>();
	private final Map<String, ReactiveBinder.Converter<?, ?>> toModelConverter = new HashMap<>();

	public ReactiveController(ReactiveComponent<C> view,C customController) {
		this.view = view;
		ReactiveBinder binder = new ReactiveBinder(this::updateModel,displayFunctions,valueReceivers,toModelConverter);
		view.createBindings(binder);
		view.createAnnotatedBindings(binder);
		view.registerListeners(customController);
	}
	private void updateModel(EventObject actionEvent) {
		Map<String, Object> changed = new HashMap<>();
		synchronized (model) {
			Field[] fields = getRelevantFields(model.getClass());
			Map<String, Field> namedFields = new HashMap<>();
			for (Field field : fields) {
				field.setAccessible(true);
				if (Modifier.isStatic(field.getModifiers()))
					continue;
				String name = getReactiveName(field);
				if (!valueReceivers.containsKey(name))
					continue;
				Object oldValue = valueCache.get(name);
				Object newValue = toModelConverter.get(name).convert(valueReceivers.get(name).get());
				if (!Objects.equals(newValue, oldValue)) {
					namedFields.put(name, field);
					changed.put(name, newValue);
				}
			}
			for (Map.Entry<String, Object> change : changed.entrySet()) {
				changeModelValue(namedFields.get(change.getKey()), change.getValue());
			}
			valueCache.putAll(changed);
			if (changed.size() > 0)
				model.react();
		}
	}

	private void changeModelValue(Field field, Object newValue) {
		try {
			FieldUtils.writeField(field,model,newValue);
		} catch (IllegalAccessException ignored) {
			ignored.printStackTrace();
		}
	}

	private static Field[] getRelevantFields(Class<?> type) {
		Field[] fields;
		if (type.isAnnotationPresent(ReactivResolution.class) && type.getDeclaredAnnotation(ReactivResolution.class).value() == ReactivResolution.ReactiveResolutions.FLAT)
			fields = type.getDeclaredFields();
		else
			fields = FieldUtils.getAllFields(type);
		return fields;
	}

	void modelChanged(){
		synchronized (model) {
			Map<String, Object> changed = new HashMap<>();

			getChanges(changed);

			modelChanged(changed);
		}
	}
	void modelChanged(Map<String, Object> changed){
		for (Map.Entry<String, Object> stringObjectEntry : changed.entrySet()) {
			updateView(stringObjectEntry.getKey(),stringObjectEntry.getValue());
		}
	}

	private void getChanges(Map<String, Object> changed) {
		Class<? extends ReactiveObject> modelClass = model.getClass();
		Field[] fields = getRelevantFields(modelClass);

		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;
			detectChange(changed, f);
		}
	}

	private void detectChange(Map<String, Object> changed, Field f) {
		f.setAccessible(true);
		String name = getReactiveName(f);
		try {
			Object value = FieldUtils.readField(f, model);
			Object oldValue = valueCache.get(name);
			if(!Objects.equals(value,oldValue)){
				valueCache.put(name,value);
				changed.put(name,value);
			}
		} catch (IllegalAccessException ignored) {
		}
	}

	private String getReactiveName(Field f) {
		return f.isAnnotationPresent(Reactive.class) ? f.getAnnotation(Reactive.class).value() : f.getName();
	}

	private void updateView(String key, Object value) {
		List<ReactiveBinder.Binding<?>> bindings = displayFunctions.get(key);
		if(bindings != null && bindings.size() > 0)
			bindings.forEach(e -> e.display(value));
	}

	public void bind(ReactiveObject model) {
		model.bind(this);
		if(this.model != null) {
			synchronized (this.model) {
				this.model = model;
				modelChanged();
			}
		}else{
			this.model = model;
			modelChanged();
		}
	}
}
