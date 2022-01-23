import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.core.impl.dsl.CoreDsl;
import com.niton.reactj.core.impl.proxy.CoreProxyBuilder;

module reactj.core.impl {
	requires transitive reactj.api;

	exports com.niton.reactj.core.impl.proxy;
	exports com.niton.reactj.core.impl.dsl to reactj.core.impl.test;

	provides ProxyBuilder with CoreProxyBuilder;
	provides BinderDsl with CoreDsl;
}