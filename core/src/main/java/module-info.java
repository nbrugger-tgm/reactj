module reactj.core {
	exports com.niton.reactj.api.annotation;
	exports com.niton.reactj.api.proxy;
	exports com.niton.reactj.api.mvc;
	exports com.niton.reactj.api.exceptions;
	exports com.niton.reactj.api.observer;
	exports com.niton.reactj.api.react;

	requires transitive reactj.observer;
	requires transitive reactj.utils;
	requires net.bytebuddy;
	requires org.objenesis;
	requires jdk.unsupported;
	requires org.apache.commons.lang3;
}