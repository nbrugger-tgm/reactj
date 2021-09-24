module reactj.core {
	exports com.niton.reactj.api.exceptions;
	exports com.niton.reactj.api.react;
	exports com.niton.reactj.api.proxy;
	exports com.niton.reactj.core.observer;
	exports com.niton.reactj.core.react;
	exports com.niton.reactj.core.annotation;
	exports com.niton.reactj.core.proxy;
	exports com.niton.reactj.core.mvc;

	requires reactj.observer;
	requires reactj.utils;
	requires net.bytebuddy;
	requires org.objenesis;
	requires jdk.unsupported;
	requires org.apache.commons.lang3;
	requires static org.junit.jupiter.api;
}