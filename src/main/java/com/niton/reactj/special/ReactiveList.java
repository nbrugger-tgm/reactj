package com.niton.reactj.special;

import com.niton.reactj.Identity;
import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveModel;
import com.niton.reactj.annotation.Reactive;
import com.niton.reactj.exceptions.ReactiveException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static com.niton.reactj.special.ListActions.*;


public interface ReactiveList<E extends Identity<?>> extends Reactable, List<E> {


	static <E extends Identity<T>,T> ReactiveList<E> create(List<E> list) {
		return (ReactiveList<E>) Proxy.newProxyInstance(
				ReactiveList.class.getClassLoader(),
				new Class[]{ReactiveList.class},
				new ReactiveListHandler<>(list));
	}

	void removeById(Object id);

	class ReactiveListHandler<E extends Identity<T>,T> implements InvocationHandler {
		private static String   addMethod;
		private static String   intAddMethod;
		private static String   removeObject;
		private static String   removeIndex;
		private static String   clear;
		private static String   setMethod;
		private static String[] justPassMethods;

		static {
			try {
				justPassMethods = new String[]{
					ReactiveList.class.getMethod("removeById", Object.class).toGenericString()
				};
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			try {
				addMethod =  List.class.getMethod("add", Object.class)
						.toGenericString();
				setMethod = List.class.getMethod("set", int.class, Object.class)
						.toGenericString();
				intAddMethod =  List.class.getMethod("add", int.class, Object.class)
						.toGenericString();

				removeObject =  List.class.getMethod("remove", Object.class)
						.toGenericString();

				removeIndex =  List.class.getMethod("remove", int.class)
						.toGenericString();

				clear =  List.class.getMethod("clear")
						.toGenericString();

			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		private final List<E>                list;
		private final ReactiveModel<List<E>> model;

		public ReactiveListHandler(List<E> list) {
			this.list = list;
			this.model = new ReactiveModel<>(list);
			model.react(INIT.id(), list);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			method.setAccessible(true);
			if(method.getDeclaringClass().toGenericString().equals(ReactiveList.class.toGenericString())){
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getID().equals(args[0])) {
						list.remove(i);
						return null;
					}
				}
				return null;
			}
			Object delegate = null;
			//Order is important as 'Object' calls (equals and such) need to be handled by the list
			if(method.getDeclaringClass().isAssignableFrom(List.class)){
				delegate = list;
			}else if (method.getDeclaringClass().isAssignableFrom(Reactable.class)) {
				delegate = model;
			}else
				throw new ReactiveException("Proxy doesnt know how to call "+method.getDeclaringClass());
			Object ret = method.invoke(delegate, args);
			//just react to list calls
			if (method.getDeclaringClass().equals(List.class)) {
				String signature = method.toGenericString();
				reactToListCall(signature, args);
			}
			return ret;
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
			}else if (signature.equals(setMethod)){
				model.react(SET_INDEX.id(), parameters[0]);
				model.react(REPLACE.id(), parameters[1]);
			}
		}
	}
}
