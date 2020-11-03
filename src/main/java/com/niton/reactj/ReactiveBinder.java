package com.niton.reactj;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReactiveBinder {
	private final UpdateFunction update;
	private final Map<String, List<Binding<?>>> displayFunctions;
	private final Map<String, ValueReceiver<?>> valueReceivers;
	private final Map<String, Converter<?, ?>> toModelConverter;

	@FunctionalInterface
	public interface UpdateFunction{
		void update(EventObject obj);
	}

	public class Binding<D>{
		DisplayFunction<D> displayer;
		Converter<?,D> converter;

		public Binding(DisplayFunction<D> displayFunctions, Converter<?,D> convertToDisplay) {
			this.displayer = displayFunctions;
			converter = convertToDisplay;
		}
		public void display(Object o){
			displayer.display(converter.convert(o));
		}
	}

	public ReactiveBinder(
			UpdateFunction update,
			Map<String, List<Binding<?>>> displayFunctions,
			Map<String, ValueReceiver<?>> valueReceivers,
			Map<String, Converter<?, ?>> toModelConverter) {
		this.update = update;
		this.displayFunctions = displayFunctions;
		this.valueReceivers = valueReceivers;
		this.toModelConverter = toModelConverter;
	}
	public <T> void bindEdit(String view, DisplayFunction<T> function, ValueReceiver<T> reciver){
		Converter<T,T> notConverter = arg -> arg;
		bindEdit(view,function,reciver, notConverter, notConverter);
	}

	public <O,T> void bindEdit(String view, DisplayFunction<O> function, ValueReceiver<O> reciver, Converter<O,T> convertToReal, Converter<T,O> convertToDisplay){
		valueReceivers.put(view,reciver);
		List<Binding<?>> funcs = displayFunctions.getOrDefault(view,new ArrayList<>());
		funcs.add(new Binding<>(function,convertToDisplay));
		displayFunctions.put(view,funcs);
		toModelConverter.put(view,convertToReal);
	}

	public void updateModel(EventObject actionEvent) {
		update.update(actionEvent);
	}


	@FunctionalInterface
	public interface DisplayFunction<R>{
		default void display(Object data){
			displayTypesave((R)data);
		}
		void displayTypesave(R data);
	}

	@FunctionalInterface
	public interface Converter<F,T>{
		default T convert(Object o){
			return convertTypesave((F)o);
		}
		T convertTypesave(F arg);
	}
	public <R> void bind(String view, DisplayFunction<R> displayFunction) {
		bind(view,displayFunction,arg->(R)arg);
	}
	public <R> void bind(String view, DisplayFunction<R> displayFunction,Converter<Object,R> transformer) {
		List<Binding<?>> funcs = displayFunctions.getOrDefault(view,new ArrayList<>());
		funcs.add(new Binding<>(displayFunction,transformer));
		displayFunctions.put(view,funcs);
	}
	@FunctionalInterface
	public interface ValueReceiver<R> {
		R get();
	}
}
