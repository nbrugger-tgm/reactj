import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.core.impl.CoreProxyBuilder;

module reactj.core.impl {
	requires reactj.api;
	provides ProxyBuilder with CoreProxyBuilder;
}