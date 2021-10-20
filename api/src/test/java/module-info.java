import com.niton.reactj.api.proxy.AbstractProxyBuilder;
import com.niton.reactj.test.proxy.MockProxyBuilder;

open module reactj.api.test {
	requires reactj.api;
	requires org.junit.jupiter.api;
	provides AbstractProxyBuilder with MockProxyBuilder;
}