import com.niton.reactj.api.proxy.ProxyBuilder;
import com.niton.reactj.test.proxy.MockProxyBuilder;

open module reactj.api.test {
	requires reactj.api;
	requires static org.junit.jupiter.api;
	requires net.bytebuddy;

	provides ProxyBuilder with MockProxyBuilder;
}