package com.niton.reactj.api.proxy;

import com.niton.reactj.api.exceptions.Exceptions;
import com.niton.reactj.api.react.Reactable;
import com.niton.reactj.api.react.ReactiveWrapper;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ServiceLoader;

/**
 * A Proxy builder does the crude job of creating a proxy for a given class using runtime
 * bytecode manipulation with Byte Buddy.
 */
public interface ProxyBuilder {
    /**
     * This suffix is appended to proxy names
     */
    String PROXY_SUFFIX     = "PROXY";
    /**
     * This is the name of the field within a proxy the origin object
     * is stored in. Can be used for reflective access
     */
    String ORIGIN_FIELD     = "PROXY_ORIGIN";
    /**
     * This is the name of the field within a proxy the {@link ReactiveWrapper}
     * is stored in.
     */
    String WRAPPER_FIELD    = "PROXY_WRAPPER";
    /**
     * A regex that matches any name of a proxy class
     */
    String PROXY_NAME_REGEX = ".+_" + PROXY_SUFFIX + "\\$[0-9]+";

    /**
     * Returns a proxy builder implementation and configures it with the given access provider.
     * <p>
     * Keep in mind that a implementation needs to be provided by the classpath/module path
     * via SPI/ServiceLoader. The default implementation is included in the core module
     * </p>
     *
     * @param accessor the access provider to use for the proxy builder
     *
     * @return the first found implementation of {@link ProxyBuilder}
     */
    static ProxyBuilder load(InfusionAccessProvider accessor) {
        var builder = ServiceLoader.load(ProxyBuilder.class)
                                   .findFirst()
                                   .orElseThrow(Exceptions.noImplementation(ProxyBuilder.class));
        builder.useInfusion(accessor);
        return builder;
    }

    /**
     * Sets the infusion access provider to use.
     */
    void useInfusion(InfusionAccessProvider accessor);

    /**
     * Constructs a base proxy class that provides an {@link Reactable} implementation.
     * You can further modify the proxy class
     * <p>
     * The proxy can handle : <br/>
     * {@link Object#equals(Object)},  {@link Object#clone()} and {@link Object#hashCode()}.<br/>
     * The proxy DOES NOT SUPPORT CLONING.
     * </p>
     * <p>
     * The proxy sends reactable calls to {@link Reactable#react()}.
     * Also the handling of java intern methods is already done.
     * Calls {@link Object} methods will never be reacted to,
     * even if they change the objects state because the should never do so.
     * </p>
     * <p>
     * This proxy is not complete and meant to be extended and built. To build use {@link
     * ReceiverTypeDefinition#make()}
     * and {@link DynamicType.Unloaded#load(ClassLoader)}
     * </p>
     *
     * @param originClass the class to create the proxy for
     * @param reactive    a descriptor which methods to react to. Elements matched by this filter
     *                    will NOT be matched if
     *                    they are matched by the exclusion filter (unreactive parameter)
     * @param unreactive  a descriptor which methods <b>not</b> to react to. Elements matched by
     *                    this filter will
     *                    never be reacted to.
     * @param <T>         the type the proxy will emulate
     *
     * @return the template for the proxy class. Can be extended using {@link ByteBuddy}
     */
    <T> ReceiverTypeDefinition<T> buildProxy(
            Class<T> originClass,
            ElementMatcher.Junction<MethodDescription> reactive,
            ElementMatcher.Junction<MethodDescription> unreactive
    );
}
