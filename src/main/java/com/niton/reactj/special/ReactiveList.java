package com.niton.reactj.special;

import com.niton.reactj.Identity;
import com.niton.reactj.Reactable;
import com.niton.reactj.mvc.ReactiveModel;
import com.niton.reactj.exceptions.ReactiveException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static com.niton.reactj.special.ListActions.*;

/**
 * Proxy creating interface. There are no implementations! You have to use ReactiveList.create
 *
 * @param <E> Type of the list as specified in {@link List}
 */
public interface ReactiveList<E> extends Reactable, List<E> {


	class ReactiveListHandler<E, T> implements InvocationHandler {
		private static String addMethod;
		private static String intAddMethod;
		private static String removeObject;
		private static String removeIndex;
		private static String clear;
		private static String setMethod;
		private static String removeById;

		static {
			try {
				removeById = ReactiveList.class.getMethod("removeById", Object.class)
				                               .toGenericString();

				addMethod = List.class.getMethod("add", Object.class)
				                      .toGenericString();

				setMethod = List.class.getMethod("set", int.class, Object.class)
				                      .toGenericString();

				intAddMethod = List.class.getMethod("add", int.class, Object.class)
				                         .toGenericString();

				removeObject = List.class.getMethod("remove", Object.class)
				                         .toGenericString();

				removeIndex = List.class.getMethod("remove", int.class)
				                        .toGenericString();

				clear = List.class.getMethod("clear")
				                  .toGenericString();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		private final List<E>                list;
		private final ReactiveModel<List<E>> model;

		public ReactiveListHandler(List<E> list) {
			this.list  = list;
			this.model = new ReactiveModel<>(list);
			model.react(INIT.id(), list);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			method.setAccessible(true);
			if (method.toGenericString().equals(removeById)) {
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

		private Object getDelegateObject(Method method) {
			Object delegate;
			if (originatesFrom(method, List.class)) {
				delegate = list;
			} else if (originatesFrom(method, Reactable.class)) {
				delegate = model;
			} else {
				throw new ReactiveException(
						"Proxy doesnt know how to call " + method.getDeclaringClass()
				);
			}
			return delegate;
		}

		/**
		 * Checks if the given method is avainable in the tree of a given type.
		 *
		 * @param method the method to check
		 * @param type   the type to check for the method
		 * @return true if type contains the method
		 */
		private boolean originatesFrom(Method method, Class<?> type) {
			return method.getDeclaringClass().isAssignableFrom(type);
		}

		private void performRemoveByID(Object[] args) {
			for (int i = 0; i < list.size(); i++) {
				if (isSameIdentity(list.get(i), args[0])) {
					list.remove(i);
				}
			}
		}

		private boolean isSameIdentity(E element, Object arg) {
			if (element instanceof Identity) {
				Identity<?> identity = (Identity<?>) element;
				return identity.getID().equals(arg);
			}
			return element.equals(arg);
		}

		private void reactToListCall(String signature, Object[] parameters) {
			if (signature.equals(addMethod)) {
				model.react(ADD.id(), parameters[0]);
			} else if (signature.equals(intAddMethod)) {
				model.react(SET_INDEX.id(), parameters[0]);
				model.react(ADD_INDEX.id(), parameters[1]);
			} else if (signature.equals(removeObject)) {
				model.react(REMOVE_OBJECT.id(), parameters[0]);
			} else if (signature.equals(removeIndex)) {
				model.react(REMOVE_INDEX.id(), parameters[0]);
			} else if (signature.equals(clear)) {
				model.react(CLEAR.id(), null);
			} else if (signature.equals(setMethod)) {
				model.react(SET_INDEX.id(), parameters[0]);
				model.react(REPLACE.id(), parameters[1]);
			}
		}
	}

	static <E, T> ReactiveList<E> create(List<E> list) {
		return (ReactiveList<E>) Proxy.newProxyInstance(
				ReactiveList.class.getClassLoader(),
				new Class[]{ReactiveList.class},
				new ReactiveListHandler<>(list));
	}

	void removeById(Object id);
}
