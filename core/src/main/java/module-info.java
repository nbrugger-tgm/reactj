module reactj.core {
	exports com.niton.reactj.core.observer;
	exports com.niton.reactj.core.react;
	exports com.niton.reactj.core.annotation;
	exports com.niton.reactj.core.proxy;
	exports com.niton.reactj.core.mvc;
	exports com.niton.reactj.core.exceptions;

	exports com.niton.reactj.api.mvc;

	exports com.niton.reactj.api.binding;
	exports com.niton.reactj.api.binding.builder;
	exports com.niton.reactj.api.binding.predicates;
	exports com.niton.reactj.api.binding.runnable;

	requires reactj.observer;
	requires reactj.utils;
	requires net.bytebuddy;
	requires org.objenesis;
	requires jdk.unsupported;
	requires org.apache.commons.lang3;
	requires static org.junit.jupiter.api;

	opens com.niton.reactj.core.proxy to reactj.observer;

}