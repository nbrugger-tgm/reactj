import com.niton.reactj.api.binding.dsl.BinderDsl;
import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.core.impl.dsl.CoreDsl;
import com.niton.reactj.core.impl.proxy.CoreProxyBuilder;

module reactj.core.impl {
	requires reactj.api;
	provides ProxyBuilder with CoreProxyBuilder;
	provides BinderDsl with CoreDsl;
}