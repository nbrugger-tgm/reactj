package com.niton.reactj.core.react;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to bind properties to reactive componnents
 *
 * @param <L> the type of the reactive model to bind to
 */
public class ReactiveBinder<L> {
	private final UpdateFunction                        update;
	private final Map<String, List<Binding<?, ?>>>      displayBindings;
	private final Map<String, List<SuperBinding<?, L>>> displaySuperBindings;
	private final List<SuperBinding<?, L>>              globalDisplaySuperBindings;
	private final Map<String, List<BiBinding<?, ?>>>    editBindings;

	@FunctionalInterface
	public interface UpdateFunction {
		void update(Object obj);
	}

	/**
	 * Used to send values to the ReactiveComponent
	 *
	 * @param <R>
	 */
	@FunctionalInterface
	public interface DisplayFunction<R> {
		@SuppressWarnings("unchecked")
		default void display(Object data) {
			displayTypesafe((R) data);
		}

		void displayTypesafe(R data);
	}

	/**
	 * Converts an object into a different type
	 *
	 * @param <F> the type to convert from
	 * @param <T> the type to convert to
	 */
	@FunctionalInterface
	public interface Converter<F, T> {
		@SuppressWarnings("unchecked")
		default T convert(Object toConvert) {
			return convertTypesafe((F) toConvert);
		}

		T convertTypesafe(F toConvert);
	}

	/**
	 * Returns a value from the components
	 *
	 * @param <R> the type to receive
	 */
	@FunctionalInterface
	public interface ValueReceiver<R> {
		R get();
	}

	@FunctionalInterface
	public interface SuperValueReceiver<R, B> {
		R get(B obj);
	}

	public static class Binding<D, F> {
		private final DisplayFunction<D> displayFunction;
		private final Converter<F, D>    toDisplayConverter;

		public Binding(
				DisplayFunction<D> displayFunctions,
				Converter<F, D> convertToDisplay
		) {
			displayFunction    = displayFunctions;
			toDisplayConverter = convertToDisplay;
		}

		public D convertToDisplay(Object value) {
			return toDisplayConverter.convert(value);
		}

		public void display(Object data) {
			displayFunction.display(data);
		}
	}

	public static class SuperBinding<T, M> {
		private final SuperValueReceiver<T, M> getter;
		private final DisplayFunction<T>       display;

		public SuperBinding(
				SuperValueReceiver<T, M> getter,
				DisplayFunction<T> display
		) {
			this.getter  = getter;
			this.display = display;
		}

		public void display(M model) {
			display.displayTypesafe(getter.get(model));
		}
	}

	public static class BiBinding<M, D> extends Binding<D, M> {
		private final ValueReceiver<D> receiver;
		private final Converter<D, M>  toModelConverter;

		public BiBinding(
				DisplayFunction<D> display,
				ValueReceiver<D> reciver,
				Converter<M, D> toDisplayConverter,
				Converter<D, M> toModelConverter
		) {
			super(display, toDisplayConverter);
			receiver              = reciver;
			this.toModelConverter = toModelConverter;
		}

		public M convertToModel(Object value) {
			return toModelConverter.convert(value);
		}

		/**
		 * @return the value from the UI converted to a value for the model
		 */
		public M getModelConverted() {
			return toModelConverter.convertTypesafe(getDisplayValue());
		}

		/**
		 * @return the plain value from the UI
		 */
		public D getDisplayValue() {
			return receiver.get();
		}
	}

	public ReactiveBinder(
			UpdateFunction update,
			Map<String, List<Binding<?, ?>>> displayBindings,
			Map<String, List<SuperBinding<?, L>>> displaySuperBindings,
			List<SuperBinding<?, L>> globalDisplaySuperBindings,
			Map<String, List<BiBinding<?, ?>>> editBindings
	) {
		this.update                     = update;
		this.displayBindings            = displayBindings;
		this.displaySuperBindings       = displaySuperBindings;
		this.globalDisplaySuperBindings = globalDisplaySuperBindings;
		this.editBindings               = editBindings;
	}

	/**
	 * {@link #bind(String, DisplayFunction, Converter)} but without conversion
	 */
	public <T> void bindBi(String view, DisplayFunction<T> function, ValueReceiver<T> reciver) {
		Converter<T, T> notConverter = arg -> arg;
		bindBi(view, function, reciver, notConverter, notConverter);
	}

	/**
	 * Binds a property bidirectional. This means that changes in the model are shown in the component and changes in
	 * the component are also forwarded to the model
	 *
	 * @param property           the name of the property to bind to
	 * @param function           the function used to change the component
	 * @param reciver            the function used to get the value from the component
	 * @param toModelConverter   the function used to convert the value from the component into the value for the model
	 * @param toDisplayConverter the reverse function to toModelConverter
	 * @param <D>                the type on the display
	 * @param <M>                the type on the model
	 */
	public <D, M> void bindBi(
			String property,
			DisplayFunction<D> function,
			ValueReceiver<D> reciver,
			Converter<D, M> toModelConverter,
			Converter<M, D> toDisplayConverter
	) {
		BiBinding<M, D> binding = new BiBinding<>(
				function,
				reciver,
				toDisplayConverter,
				toModelConverter
		);
		List<Binding<?, ?>> funcs = displayBindings.getOrDefault(property, new ArrayList<>());
		funcs.add(binding);
		displayBindings.put(property, funcs);
		List<BiBinding<?, ?>> biBindings = editBindings.getOrDefault(property, new ArrayList<>());
		biBindings.add(binding);
		editBindings.put(property, biBindings);
	}

	/**
	 * Call this method to inform the bindings that the component has changed
	 *
	 * @param actionEvent not used, just there to enable method references
	 */
	public void react(Object actionEvent) {
		update.update(actionEvent);
	}

	public void react() {
		update.update(null);
	}

	public <R> void bind(String view, DisplayFunction<R> displayFunction) {
		bind(view, displayFunction, (R arg) -> arg);
	}

	/**
	 * Binds a property to the component. This means that the component is allways up to date with the model
	 *
	 * @param property        the name of the property to react to
	 * @param displayFunction the function used to change the component
	 * @param transformer     used to convert from the model value to a value for the component
	 * @param <F>             the type of the property in the model
	 * @param <D>             the type of the prperty in the component
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
	 * Binds a getter function to a display function. This getter is supplied with the whole model and therefore can
	 * assemble a value.
	 * <br/>
	 * For example if the class Person has a name and surename the getter could be {@code (Person
	 * p)->p.getName()+p.getSurename()}<br/>
	 * <p>
	 * This binding is executed on EVERY change to the model! Due to this being the case, this method is more resource
	 * expensive than {@link #bind(SuperValueReceiver, DisplayFunction, String...)} so consider using it instead.
	 *
	 * @param getter          a function calculating a value from a reactive object
	 * @param displayFunction the function to display the value
	 * @param <D>             the type to display
	 */
	public <D> void bind(
			SuperValueReceiver<D, L> getter,
			DisplayFunction<D> displayFunction
	) {
		globalDisplaySuperBindings.add(new SuperBinding<>(getter, displayFunction));
	}

	/**
	 * Works the same as {@link #bind(SuperValueReceiver, DisplayFunction)} with the minor difference that it only
	 * reacts on the change of certain properties
	 *
	 * @param getter          the function used to contruct a value from the model
	 * @param displayFunction the function to display the value
	 * @param triggers        the names of the properties, when changed triggering this binding
	 * @param <D>             the type of the displayed values
	 */
	public <D> void bind(
			SuperValueReceiver<D, L> getter,
			DisplayFunction<D> displayFunction,
			String... triggers
	) {
		for (String trigger : triggers) {
			List<SuperBinding<?, L>> bindings = displaySuperBindings.getOrDefault(
					trigger,
					new ArrayList<>()
			);
			bindings.add(new SuperBinding<>(getter, displayFunction));
			displaySuperBindings.put(trigger, bindings);
		}
	}

	/**
	 * Bind the visibility of a UI element to a condition about the model
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
	 * Simmilar to the {@code v-if} directive in VUE JS. Creates a binding to show a component only in certain
	 * circumstances
	 *
	 * @param property       the name of the property to base this decition on
	 * @param enableFunction the function used to enable/disable the component
	 */
	public void showIf(String property, DisplayFunction<Boolean> enableFunction) {
		bind(property, enableFunction, b -> (boolean) b);
	}
}
