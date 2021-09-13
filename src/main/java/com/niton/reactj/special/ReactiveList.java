package com.niton.reactj.special;

import com.niton.reactj.Reactable;
import com.niton.reactj.exceptions.ReactiveException;
import com.niton.reactj.proxy.ReactiveProxyEngine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Proxy creating interface. There are no implementations! You have to use //todo//
 * <p>
 * This class wraps a list, it isnt a list implementation it just makes a list implementation (for example ArrayList)
 * reactive
 *
 * @param <E> Type of the list as specified in {@link List}
 */
public interface ReactiveList<E> extends Reactable, List<E> {


	class ReactiveListHandler<E> implements InvocationHandler {
		private static final String ADD_METHOD;
		private static final String INT_ADD_METHOD;
		private static final String REMOVE_OBJECT;
		private static final String REMOVE_INDEX;
		private static final String CLEAR;
		private static final String SET_METHOD;
		private static final String REMOVE_BY_ID;

		static {
			try {
				REMOVE_BY_ID = ReactiveList.class.getMethod("removeById", Object.class)
				                                 .toGenericString();

				ADD_METHOD = List.class.getMethod("add", Object.class)
				                       .toGenericString();

				SET_METHOD = List.class.getMethod("set", int.class, Object.class)
				                       .toGenericString();

				INT_ADD_METHOD = List.class.getMethod("add", int.class, Object.class)
				                           .toGenericString();

				REMOVE_OBJECT = List.class.getMethod("remove", Object.class)
				                          .toGenericString();

				REMOVE_INDEX = List.class.getMethod("remove", int.class)
				                         .toGenericString();

				CLEAR = List.class.getMethod("clear")
				                  .toGenericString();
			} catch (NoSuchMethodException e) {
				//will not be reached. If reached only because java radically changed
				throw new LinkageError("Expected methods not found in java.lang.List class", e);
			}
		}

		private final List<E>                      list;
		private final ReactiveProxyEngine<List<E>> model;

		public ReactiveListHandler(List<E> list) {
			this.list = list;
			model = new ReactiveProxyEngine<>(list);
			//model.react(INIT.id(), list);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			method.setAccessible(true);
			if (method.toGenericString().equals(REMOVE_BY_ID)) {
				performRemoveByID(args);
				return null;
			}
			Object delegate    = getDelegateObject(method);
			Object returnValue = method.invoke(delegate, args);

			//just react to list calls
			if (method.getDeclaringClass().equals(List.class)) {
				String signature = method.toGenericString();
				reactToListCall(signature, args);
			}
			return returnValue;
		}

		private void performRemoveByID(Object... args) {
			for (int i = 0; i < list.size(); i++) {
				if (isSameIdentity(list.get(i), args[0])) {
					list.remove(i);
					reactToListCall(REMOVE_INDEX, i);
				}
			}
		}

		private Object getDelegateObject(Method method) {
			Object delegate;
			if (originatesFrom(method, List.class)) {
				delegate = list;
			} else if (originatesFrom(method, Reactable.class)) {
				delegate = model;
			} else {
				throw new ReactiveException(
						"List Proxy doesnt know how to call " + method.getDeclaringClass()
				);
			}
			return delegate;
		}

		private void reactToListCall(String signature, Object... parameters) {
			/*if(signature.equals(ADD_METHOD)) {
				model.react(ADD.id(), parameters[0]);
			} else if(signature.equals(INT_ADD_METHOD)) {
				model.react(SET_INDEX.id(), parameters[0]);
				model.react(ADD_INDEX.id(), parameters[1]);
			} else if(signature.equals(REMOVE_OBJECT)) {
				model.react(ListAction.REMOVE_OBJECT.id(), parameters[0]);
			} else if(signature.equals(REMOVE_INDEX)) {
				model.react(ListAction.REMOVE_INDEX.id(), parameters[0]);
			} else if(signature.equals(CLEAR)) {
				model.react(ListAction.CLEAR.id(), null);
			} else if(signature.equals(SET_METHOD)) {
				model.react(SET_INDEX.id(), parameters[0]);
				model.react(REPLACE.id(), parameters[1]);
			}*/
		}

		private boolean isSameIdentity(E element, Object arg) {
			if (element instanceof Identity) {
				Identity<?> identity = (Identity<?>) element;
				if (arg instanceof Identity) {
					return identity.getID().equals(((Identity<?>) arg).getID());
				} else {
					return identity.getID().equals(arg);
				}
			}
			return element.equals(arg);
		}

		/**
		 * Checks if the given method is avainable in the tree of a given type.
		 *
		 * @param method the method to check
		 * @param type   the type to check for the method
		 *
		 * @return true if type contains the method
		 */
		private boolean originatesFrom(Method method, Class<?> type) {
			return method.getDeclaringClass().isAssignableFrom(type);
		}
	}

	/**
	 * Creates an reactive list from an existing list.
	 * The new reactive list is returned.
	 * <p>
	 * After creating a reactive version of a list, DO NOT use the old list reference
	 * <p>
	 * {@code
	 * ArrayList<int> lst = new ArrayList<>();
	 * lst.add(value1);//This is allowed
	 * ReactiveList<int> reactive = ReactiveList.create(lst);
	 * lst.add(value2);//DONT! do this
	 * }
	 *
	 * @param list the list to make reactive
	 * @param <E>  the type of the list
	 *
	 * @return the reactive list
	 */
	static <E> ReactiveList<E> create(List<E> list) {
		return (ReactiveList<E>) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class[]{ReactiveList.class},
				new ReactiveListHandler<>(list)
		);
	}

	void removeById(Object id);
}
