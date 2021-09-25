package com.niton.reactj.lists.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.infusion.StaticInfuserWithLookup;
import com.niton.reactj.utils.exceptions.ReflectiveCallException;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.lang.String.format;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ListProxyCreator extends AbstractProxyCreator {
	private static final Method[] listModifyingMethods;

	static {
		try {
			Class<?> list = List.class;
			listModifyingMethods = new Method[]{
					list.getDeclaredMethod("set", int.class, Object.class),
					list.getDeclaredMethod("add", Object.class),
					list.getDeclaredMethod("add", int.class, Object.class),
					list.getDeclaredMethod("addAll", Collection.class),
					list.getDeclaredMethod("addAll", int.class, Collection.class),
					list.getDeclaredMethod("clear"),
					list.getDeclaredMethod("remove", int.class),
					list.getDeclaredMethod("remove", Object.class),
					list.getDeclaredMethod("removeAll", Collection.class),
					list.getDeclaredMethod("retainAll", Collection.class),
					list.getDeclaredMethod("replaceAll", UnaryOperator.class),
					list.getDeclaredMethod("sort", Comparator.class)
			};
		} catch (Exception e) {
			throw new ReactiveException("Failed to prefetch list operations, java 11 should help out", e);
		}
	}

	public ListProxyCreator() {
		super(new StaticInfuserWithLookup(ListProxyCreator.class, MethodHandles.lookup()));
	}

	public <L extends List<T>, T> L create(L list) {
		Class<?> proxyClass = getProxyClass(list.getClass());
		try {
			@SuppressWarnings("unchecked")
			L proxyList = (L) proxyClass.getDeclaredConstructor().newInstance();
			this.setProxyFields(list, proxyClass, proxyList);
			return proxyList;
		} catch (InstantiationException e) {
			throw new ReflectiveCallException("Failed instantiating List", e);
		} catch (IllegalAccessException e) {
			throw new ReflectiveCallException(
					format("Couldn't access parameterless constructor of %s", proxyClass.getSuperclass().getName())
			);
		} catch (InvocationTargetException e) {
			throw new ReflectiveCallException("Call failed", e);
		} catch (NoSuchMethodException e) {
			throw new ReflectiveCallException(
					format("Unable to find parameterless Contructor of %s", proxyClass.getSuperclass().getName())
			);
		}
	}

	@Override
	protected <T> Class<? extends T> createProxyClass(Class<? extends T> listType) {
		Module module = listType.getModule();
		Lookup lookup = getLookup(listType);

		return getBuilder().buildProxy(
						listType,
						overwritesAnyOf(listModifyingMethods),
						none()
				)
				.make().load(module.getClassLoader(), UsingLookup.of(lookup))
				.getLoaded();
	}

	//TODO: solve ... better
	public ElementMatcher.Junction<MethodDescription> overwritesAnyOf(final Method[] abstractMethods) {
		return new ElementMatcher.Junction.AbstractBase<>() {
			@Override
			public boolean matches(MethodDescription target) {
				for (Method abstractMethod : abstractMethods) {
					if (overwrites(abstractMethod).matches(target))
						return true;
				}
				return false;
			}
		};
	}

	private ElementMatcher.Junction<MethodDescription> overwrites(Method abstractMethod) {
		return named(abstractMethod.getName()).and(returns(abstractMethod.getReturnType())).and(takesArguments(abstractMethod.getParameterTypes()));
	}
}
