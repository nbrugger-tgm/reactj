package com.niton.reactj.core.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.proxy.infusion.BesideOriginInfuser;
import com.niton.reactj.api.proxy.infusion.InfusionAccessProvider;
import com.niton.reactj.api.proxy.infusion.StaticInfuser;
import com.niton.reactj.api.proxy.infusion.StaticInfuserWithLookup;
import com.niton.reactj.core.annotation.Unreactive;
import com.niton.reactj.core.observer.Reflective;
import com.niton.reactj.core.observer.ReflectiveWrapper;
import com.niton.reactj.utils.reflections.ReflectiveUtil;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;
import net.bytebuddy.implementation.FieldAccessor;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.niton.reactj.api.proxy.ProxyBuilder.ORIGIN_FIELD;
import static com.niton.reactj.observer.util.Matchers.from;
import static net.bytebuddy.implementation.DefaultMethodCall.prioritize;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ProxyCreator extends AbstractProxyCreator {
	public static final ProxyCreator INSTANCE = besideOrigin();
	static final        Method       getReflectiveTargetMethod;

	static {
		try {
			getReflectiveTargetMethod = ReflectiveWrapper.class.getDeclaredMethod("getReflectiveTarget");
		} catch (NoSuchMethodException e) {
			throw new ReactiveException("FATAL: react method not loadable!", e);
		}
	}

	private final Objenesis                            objenesis       = new ObjenesisStd();
	private final Map<Class<?>, ObjectInstantiator<?>> proxyInitiators = new HashMap<>();
	private       ReactiveStrategy                     strategy        = ReactiveStrategy.ALL;

	protected ProxyCreator(InfusionAccessProvider accessor) {
		super(accessor);
	}

	/**
	 * Stores created proxies within reactj. Therefore only exports access is needed. But the proxy cant contain
	 * any foreign classes, just the modules reactj depends on AND the module the origin class originates from
	 *
	 * @return a very permissive ProxyCreator
	 */
	public static ProxyCreator withinDependency() {
		return new ProxyCreator(new StaticInfuserWithLookup(ProxyCreator.class, MethodHandles.lookup()));
	}

	/**
	 * Stores proxies besides the orgin they impose. Needs open to reactj access (for every origin package).
	 * <p><b>
	 * Only one instance should be used per module!
	 * </b></p>
	 *
	 * @return an ProxyCreator that will have no issues with permissons if you open your module (to reactj)
	 */
	public static ProxyCreator besideOrigin() {
		return new ProxyCreator(new BesideOriginInfuser(MethodHandles.lookup()));
	}

	/**
	 * Creates proxies beside the given class. Needs open permissions on the package
	 *
	 * @param anchor the class to spawn the proxies nearby
	 *
	 * @return an Proxie creator that just needs a single open stanement
	 */
	public static ProxyCreator custom(Class<?> anchor) {
		return new ProxyCreator(new StaticInfuser(anchor, MethodHandles.lookup()));
	}

	public static ProxyCreator custom(Class<?> anchor, Lookup access) {
		return new ProxyCreator(new StaticInfuserWithLookup(anchor, access));
	}

	/**
	 * Changing the strategy will <b>not</b> affect already created proxies!
	 *
	 * @param strategy the strategy to use for newly created proxies
	 */
	public void setStrategy(ReactiveStrategy strategy) {
		this.strategy = strategy;
	}

	public <T extends ProxySubject> T create(T object) {
		return createProxy(object);
	}

	private <T> T createProxy(T object) {
		Class<?>              originClass = object.getClass();
		Class<?>              proxyClass  = getProxyClass(originClass);
		ObjectInstantiator<?> initiator   = proxyInitiators.computeIfAbsent(proxyClass, objenesis::getInstantiatorOf);

		@SuppressWarnings("unchecked")
		T proxy = (T) initiator.newInstance();

		setProxyFields(object, proxyClass, proxy);
		copyFinalFields(proxy, object);
		return proxy;
	}

	/**
	 * Copies the values of public final fields from origin to proxy
	 */
	public <T> void copyFinalFields(T proxy, T origin) {
		Arrays.stream(proxy.getClass().getFields())//just public ones
		      .filter(f -> Modifier.isFinal(f.getModifiers()))//just final ones
		      .forEach(f -> copyFinalField(f, proxy, origin));
	}

	public <T> void copyFinalField(Field f, T proxy, T origin) {
		try {
			ReflectiveUtil.setFinal(f, proxy, f.get(origin));
		} catch (IllegalAccessException | NoSuchFieldException e) {
			throw new ReactiveException("Couldn't copy final field", e);
		}
	}

	public <T> ReactiveProxy<T> create(T object) {
		return new ReactiveProxy<>(createProxy(object));
	}

	protected <T> Class<? extends T> createProxyClass(Class<? extends T> originClass) throws ProxyException {

		Module module = originClass.getModule();
		getClass().getModule().addReads(module);

		var unreactive = isAnnotatedWith(Unreactive.class)
				.or(from(Reflective.class))
				.or(from(ReflectiveWrapper.class));

		var prox = getBuilder().buildProxy(originClass, any(), unreactive)
		                       .implement(ReflectiveWrapper.class)

		                       .method(from(Reflective.class))
		                       .intercept(prioritize(ReflectiveWrapper.class))

		                       .method(is(getReflectiveTargetMethod))
		                       .intercept(FieldAccessor.ofField(ORIGIN_FIELD))

		                       .make();
		var lookup = getLookup(originClass);
		return prox.load(module.getClassLoader(), UsingLookup.of(lookup)).getLoaded();
	}
}
