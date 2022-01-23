package com.niton.reactj.lists.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.exceptions.ReflectiveCallException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.proxy.infusion.StaticInfuser;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.UnaryOperator;

import static com.niton.reactj.api.util.Matchers.overwritesAnyOf;
import static net.bytebuddy.matcher.ElementMatchers.none;

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
		} catch (NoSuchMethodException e) {
			throw new ReactiveException(
					"Failed to prefetch list operations, java 11 should help out",
					e
			);
		}
	}

	public ListProxyCreator(MethodHandles.Lookup proxyAnchor) {
		super(new StaticInfuser(proxyAnchor.lookupClass(), proxyAnchor));
	}

	public <L extends List<T>, T> L create(L list) {
		Class<?> proxyClass = getProxyClass(list.getClass());
		try {
			@SuppressWarnings("unchecked")
			L proxyList = (L) proxyClass.getDeclaredConstructor().newInstance();
			setProxyFields(list, proxyClass, proxyList);
			return proxyList;
		} catch (InstantiationException | InvocationTargetException e) {
			throw new ReflectiveCallException("Failed instantiating List", e);
		} catch (IllegalAccessException e) {
			throw ProxyException.constructorAccessException(proxyClass);
		} catch (NoSuchMethodException e) {
			throw ProxyException.noParameterlessConstructor(proxyClass);
		}
	}

	@Override
	protected <T> Class<? extends T> createProxyClass(Class<? extends T> listType) {
		Module module = listType.getModule();
		Lookup lookup = getLookup(listType);

		return getBuilder()
				.buildProxy(
						listType,
						overwritesAnyOf(listModifyingMethods),
						none()
				)
				.make()
				.load(module.getClassLoader(), UsingLookup.of(lookup))
				.getLoaded();
	}

}
