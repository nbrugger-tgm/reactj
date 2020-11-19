package com.niton.reactj;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

public class ReactiveBinder {
	private final UpdateFunction                     update;
	private final Map<String, List<Binding<?, ?>>>   displayBindings;
	private final Map<String, List<BiBinding<?, ?>>> editBindings;

	@FunctionalInterface
	public interface UpdateFunction {
		void update(EventObject obj) throws Throwable;
	}

	@FunctionalInterface
	public interface DisplayFunction<R> {
		default void display(Object data) {
			displayTypesave((R) data);
		}

		void displayTypesave(R data);
	}

	@FunctionalInterface
	public interface Converter<F, T> {
		default T convert(Object o) {
			return convertTypesave((F) o);
		}

		T convertTypesave(F arg);
	}

	@FunctionalInterface
	public interface ValueReceiver<R> {
		R get();
	}

	public static class Binding<D, F> {
		private final DisplayFunction<D> display;
		private final Converter<F, D>    toDisplayConverter;

		public Binding(
				DisplayFunction<D> displayFunctions,
				Converter<F, D> convertToDisplay
		) {
			this.display = displayFunctions;
			toDisplayConverter = convertToDisplay;
		}

		public DisplayFunction<D> getDisplay() {
			return display;
		}

		public Converter<F, D> getToDisplayConverter() {
			return toDisplayConverter;
		}
	}

	public static class BiBinding<M, D> extends Binding<D, M> {
		final ValueReceiver<D> reciver;
		final Converter<D, M>  toModelConverter;

		public BiBinding(
				DisplayFunction<D> display,
				ValueReceiver<D> reciver,
				Converter<M, D> toDisplayConverter,
				Converter<D, M> toModelConverter
		) {
			super(display, toDisplayConverter);
			this.reciver = reciver;
			this.toModelConverter = toModelConverter;
		}
	}


	public ReactiveBinder(
			UpdateFunction update,
			Map<String, List<Binding<?, ?>>> displayBindings,
			Map<String, List<BiBinding<?, ?>>> editBindings
	) {
		this.update = update;
		this.displayBindings = displayBindings;
		this.editBindings = editBindings;
	}

	public <T> void bindBi(String view, DisplayFunction<T> function, ValueReceiver<T> reciver) {
		Converter<T, T> notConverter = arg -> arg;
		bindBi(view, function, reciver, notConverter, notConverter);
	}

	public <D, M> void bindBi(
			String view,
			DisplayFunction<D> function,
			ValueReceiver<D> reciver,
			Converter<D, M> toModelConverter,
			Converter<M, D> toDisplayConverter
	) {
		BiBinding<M, D> binding = new BiBinding<>(function, reciver, toDisplayConverter, toModelConverter);
		List<Binding<?, ?>> funcs = displayBindings.getOrDefault(view, new ArrayList<>());
		funcs.add(binding);
		displayBindings.put(view, funcs);
		List<BiBinding<?, ?>> biBindings = editBindings.getOrDefault(view, new ArrayList<>());
		biBindings.add(binding);
		editBindings.put(view, biBindings);
	}

	public void react(EventObject actionEvent) {
		try {
			update.update(actionEvent);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public <R> void bind(String view, DisplayFunction<R> displayFunction) {
		bind(view, displayFunction, arg -> (R) arg);
	}

	public <F,R> void bind(String view, DisplayFunction<R> displayFunction, Converter<F, R> transformer) {
		List<Binding<?, ?>> funcs = displayBindings.getOrDefault(view, new ArrayList<>());
		funcs.add(new Binding<>(displayFunction, transformer));
		displayBindings.put(view, funcs);
	}

	/**
	 * Bind the visibility of an UI element to a condition about the model
	 * @param property the name of property the condition will be based on
	 * @param enableFunction the function reference to enable or disable the UI component
	 * @param condition a function that results in a boolean
	 * @param <M> the type (of the property) present in the model
	 */
	public <M> void showIf(String property, DisplayFunction<Boolean> enableFunction,Converter<M,Boolean> condition){
		bind(property,enableFunction,condition);
	}

	public <M> void showIf(String view, DisplayFunction<Boolean> enableFunction){
		bind(view,enableFunction,b -> (boolean)b);
	}
}
