module reactj.core {
	requires reactj.observer;
	requires reactj.utils;
	requires net.bytebuddy;
	requires org.objenesis;
	requires jdk.unsupported;
	requires org.apache.commons.lang3;
	requires static org.junit.jupiter.api;
	requires reactj.lists;

	exports com.niton.reactj.core.observer;
	exports com.niton.reactj.core.react;
	exports com.niton.reactj.core.annotation;
	exports com.niton.reactj.core.proxy;
	exports com.niton.reactj.core.exceptions;
	exports com.niton.reactj.core.mvc;

	exports com.niton.reactj.api.mvc;

	exports com.niton.reactj.api.binding;
	exports com.niton.reactj.api.binding.builder;
	exports com.niton.reactj.api.binding.predicates;
	exports com.niton.reactj.api.binding.runnable;

	opens com.niton.reactj.core.proxy to reactj.observer;

	exports com.niton.reactj.api.binding.builder.exposed;
	exports com.niton.reactj.api.binding.builder.conditional;

	exports com.niton.reactj.testing.mvc;
	opens com.niton.reactj.testing.mvc to org.junit.platform.commons;

}