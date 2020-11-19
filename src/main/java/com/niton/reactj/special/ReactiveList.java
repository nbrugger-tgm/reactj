package com.niton.reactj.special;

import com.niton.reactj.Reactable;
import com.niton.reactj.ReactiveModel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static com.niton.reactj.special.ListActions.*;


public interface ReactiveList<E> extends Reactable, List<E> {


	static <E> ReactiveList<E> create(List<E> list) {
		return (ReactiveList<E>) Proxy.newProxyInstance(
				ReactiveList.class.getClassLoader(),
				new Class[]{ReactiveList.class},
				new ReactiveListHandler<>(list));
	}

	class ReactiveListHandler<E> implements InvocationHandler {
		private static String
				addMethod ,
				intAddMethod ,
				removeObject ,
				removeIndex,
				clear;

		static {
			try {
				addMethod =  List.class.getMethod("add", Object.class)
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
			Object ret;
			if (method.getDeclaringClass().equals(Reactable.class)) {
				method.setAccessible(true);
				ret = method.invoke(model, args);
			} else {
				method.setAccessible(true);
				ret = method.invoke(list, args);
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
			}
		}
	}
}
