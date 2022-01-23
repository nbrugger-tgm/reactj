package com.niton.reactj.objects.proxy;

import com.niton.reactj.api.exceptions.ReactiveException;
import com.niton.reactj.api.proxy.AbstractProxyCreator;
import com.niton.reactj.api.proxy.InfusionAccessProvider;
import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.api.proxy.ProxyException;
import com.niton.reactj.api.proxy.infusion.BesideOriginInfuser;
import com.niton.reactj.api.proxy.infusion.StaticInfuser;
import com.niton.reactj.api.proxy.infusion.StaticInfuserWithLookup;
import com.niton.reactj.api.util.Matchers;
import com.niton.reactj.api.util.ReflectiveUtil;
import com.niton.reactj.objects.ReactiveStrategy;
import com.niton.reactj.objects.reflect.Reflective;
import com.niton.reactj.objects.reflect.ReflectiveWrapper;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.UsingLookup;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class ProxyCreator extends AbstractProxyCreator {
    public static final  ProxyCreator INSTANCE = besideOrigin();
    private static final Method       getReflectiveTargetMethod;
    private static final Logger       LOG      = LoggerFactory.getLogger(ProxyCreator.class);

    static {
        try {
            getReflectiveTargetMethod = ReflectiveWrapper.class.getDeclaredMethod(
                    "getReflectiveTarget");
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
     * Stores created proxies within reactj. Therefore, only exports access is needed. But the proxy
     * can't contain
     * any foreign classes, just the modules reactj depends on AND the module the origin class
     * originates from
     *
     * @return a very permissive ProxyCreator
     */
    public static ProxyCreator withinDependency() {
        return new ProxyCreator(new StaticInfuserWithLookup(
                ProxyCreator.class,
                MethodHandles.lookup()
        ));
    }

    /**
     * Stores proxies besides the origin they impose. Needs open to reactj access (for every
     * origin package).
     * <p><b>
     * Only one instance should be used per module!
     * </b></p>
     *
     * @return an ProxyCreator that will have no issues with permissions if you open your module
     * (to reactj)
     */
    public static ProxyCreator besideOrigin() {
        return new ProxyCreator(new BesideOriginInfuser(MethodHandles.lookup()));
    }

    /**
     * Creates proxies beside the given class. Needs open permissions on the package
     *
     * @param anchor the class to spawn the proxies nearby
     *
     * @return a proxy creator that just needs a single open statement
     */
    public static ProxyCreator custom(Class<?> anchor) {
        return new ProxyCreator(new StaticInfuser(anchor, MethodHandles.lookup()));
    }

    public static ProxyCreator custom(Class<?> anchor, Lookup access) {
        return new ProxyCreator(new StaticInfuserWithLookup(anchor, access));
    }

    protected <T> Class<? extends T> createProxyClass(Class<? extends T> originClass)
            throws ProxyException {

        Module module = originClass.getModule();
        getClass().getModule().addReads(module);

        var unreactive = Matchers.from(Reflective.class)
                                 .or(Matchers.from(ReflectiveWrapper.class));

        var proxy = getBuilder().buildProxy(originClass, strategy.matcher, unreactive)
                                .implement(ReflectiveWrapper.class)

                                .method(Matchers.from(Reflective.class))
                                .intercept(DefaultMethodCall.prioritize(ReflectiveWrapper.class))

                                .method(ElementMatchers.is(getReflectiveTargetMethod))
                                .intercept(FieldAccessor.ofField(ProxyBuilder.ORIGIN_FIELD))

                                .make();
        var lookup = getLookup(originClass);
        return proxy.load(module.getClassLoader(), UsingLookup.of(lookup)).getLoaded();
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
        Class<?> originClass = object.getClass();
        Class<?> proxyClass  = getProxyClass(originClass);
        ObjectInstantiator<?> initiator = proxyInitiators.computeIfAbsent(
                proxyClass,
                objenesis::getInstantiatorOf
        );

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
        try {
            Arrays.stream(proxy.getClass().getFields())//just public ones
                  .filter(f -> Modifier.isFinal(f.getModifiers()))//just final ones
                  .filter(f -> !copyFinalField(f, proxy, origin))//filter uncopiable ones
                  .forEach(b -> LOG.warn(
                          "Cannot copy public final field {} to proxy, accessing " +
                                  "it on the proxy will return null!",
                          b.getName()
                  ));
        } catch (ReactiveException ex) {
            LOG.warn("Final field copy failed!", ex);
        }
    }

    public <T> boolean copyFinalField(Field field, T proxy, T origin) {
        try {
            return ReflectiveUtil.setFinal(field, proxy, field.get(origin));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ReactiveException("Couldn't copy field", e);
        }
    }

    public <T> ReactiveProxy<T> create(T object) {
        return new ReactiveProxy<>(createProxy(object));
    }
}
