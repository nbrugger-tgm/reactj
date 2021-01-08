package com.niton.reactj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to bind properties to reactive componnents
 */
public class ReactiveBinder {
	private final UpdateFunction                     update;
	private final Map<String, List<Binding<?, ?>>>   displayBindings;
	private final Map<String, List<BiBinding<?, ?>>> editBindings;

	@FunctionalInterface
	public interface UpdateFunction {
		void update(Object obj);
	}

	/**
	 * Used to send values to the ReactiveComponent
	 * @param <R>
	 */
	@FunctionalInterface
	public interface DisplayFunction<R> {
		default void display(Object data) {
			displayTypesave((R) data);
		}

		void displayTypesave(R data);
	}

	/**
	 * Converts an object into a different type
	 * @param <F> the type to convert from
	 * @param <T> the type to convert to
	 */
	@FunctionalInterface
	public interface Converter<F, T> {
		default T convert(Object toConvert) {
			return convertTypesave((F) toConvert);
		}

		T convertTypesave(F toConvert);
	}

	/**
	 * Returns a value from the components
	 * @param <R> the type to receive
	 */
	@FunctionalInterface
	public interface ValueReceiver<R> {
		R get();
	}

	public static class Binding<D, F> {
		private final DisplayFunction<D> displayFunction;
		private final Converter<F, D>    toDisplayConverter;

		public Binding(
				DisplayFunction<D> displayFunctions,
				Converter<F, D> convertToDisplay
		) {
			this.displayFunction = displayFunctions;
			toDisplayConverter   = convertToDisplay;
		}

		public D convertToDisplay(Object value) {
			return toDisplayConverter.convert(value);
		}

		public void display(Object data) {
			displayFunction.display(data);
		}

		public DisplayFunction<D> getDisplayFunction() {
			return displayFunction;
		}

		public Converter<F, D> getToDisplayConverter() {
			return toDisplayConverter;
		}
	}

	public static class BiBinding<M, D> extends Binding<D, M> {
		private final ValueReceiver<D> reciver;
		private final Converter<D, M>  toModelConverter;

		public M convertToModel(Object value) {
			return toModelConverter.convert(value);
		}

		public D get() {
			return reciver.get();
		}

		public BiBinding(
				DisplayFunction<D> display,
				ValueReceiver<D> reciver,
				Converter<M, D> toDisplayConverter,
				Converter<D, M> toModelConverter
		) {
			super(display, toDisplayConverter);
			this.reciver          = reciver;
			this.toModelConverter = toModelConverter;
		}

		public Converter<D, M> getToModelConverter() {
			return toModelConverter;
		}

		public ValueReceiver<D> getReciver() {
			return reciver;
		}

		/**
		 * @return the value from the UI converted to a value for the model
		 */
		public M getModelConverted() {
			return toModelConverter.convertTypesave(get());
		}
	}


	public ReactiveBinder(
			UpdateFunction update,
			Map<String, List<Binding<?, ?>>> displayBindings,
			Map<String, List<BiBinding<?, ?>>> editBindings
	) {
		this.update          = update;
		this.displayBindings = displayBindings;
		this.editBindings    = editBindings;
	}

	/**
	 * {@link #bind(String, DisplayFunction, Converter)} but without conversion
	 */
	public <T> void bindBi(String view, DisplayFunction<T> function, ValueReceiver<T> reciver) {
		Converter<T, T> notConverter = arg -> arg;
		bindBi(view, function, reciver, notConverter, notConverter);
	}

	/**
	 * Binds a property bidirectional. This means that changes in the model are shown in the component and changes in the component are also forwarded to the model
	 * @param property the name of the property to bind to
	 * @param function the function used to change the component
	 * @param reciver the function used to get the value from the component
	 * @param toModelConverter the function used to convert the value from the component into the value for the model
	 * @param toDisplayConverter the reverse function to toModelConverter
	 * @param <D> the type on the display
	 * @param <M> the type on the model
	 */
	public <D, M> void bindBi(
			String property,
			DisplayFunction<D> function,
			ValueReceiver<D> reciver,
			Converter<D, M> toModelConverter,
			Converter<M, D> toDisplayConverter
	) {
		BiBinding<M, D> binding = new BiBinding<>(function,
		                                          reciver,
		                                          toDisplayConverter,
		                                          toModelConverter);
		List<Binding<?, ?>> funcs = displayBindings.getOrDefault(property, new ArrayList<>());
		funcs.add(binding);
		displayBindings.put(property, funcs);
		List<BiBinding<?, ?>> biBindings = editBindings.getOrDefault(property, new ArrayList<>());
		biBindings.add(binding);
		editBindings.put(property, biBindings);
	}

	/**
	 * Call this method to inform the bindings that the component has changed
	 * @param actionEvent not used, just there to enable method references
	 */
	public void react(Object actionEvent) {
		update.update(actionEvent);
	}

	public void react(){
		update.update(null);
	}

	public <R> void bind(String view, DisplayFunction<R> displayFunction) {
		bind(view, displayFunction, arg -> (R) arg);
	}

	/**
	 * Binds a property to the component. This means that the component is allways up to date with the model
	 * @param property the name of the property to react to
	 * @param displayFunction the function used to change the component
	 * @param transformer used to convert from the model value to a value for the component
	 * @param <F> the type of the property in the model
	 * @param <D> the type of the prperty in the component
	 */
	public <F, D> void bind(
			String property,
			DisplayFunction<D> displayFunction,
			Converter<F, D> transformer
	) {
		List<Binding<?, ?>> funcs = displayBindings.getOrDefault(property, new ArrayList<>());
		funcs.add(new Binding<>(displayFunction, transformer));
		displayBindings.put(property, funcs);
	}

	/**
	 * Bind the visibility of an UI element to a condition about the model
	 *
	 * @param property       the name of property the condition will be based on
	 * @param enableFunction the function reference to enable or disable the UI component
	 * @param condition      a function that results in a boolean
	 * @param <M>            the type (of the property) present in the model
	 */
	public <M> void showIf(
			String property,
			DisplayFunction<Boolean> enableFunction,
			Converter<M, Boolean> condition
	) {
		bind(property, enableFunction, condition);
	}

	/**
	 * Simmilar to the {@code v-if} directive in VUE JS. Creates a binding to show a component only in certain circumstances
	 * @param property the name of the property to base this decition on
	 * @param enableFunction the function used to enable/disable the component
	 */
	public void showIf(String property, DisplayFunction<Boolean> enableFunction) {
		bind(property, enableFunction, b -> (boolean) b);
	}
}
