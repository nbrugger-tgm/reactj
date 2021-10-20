module reactj.api {
	requires static org.junit.jupiter.api;
	requires net.bytebuddy;

	exports com.niton.reactj.api.binding.builder.conditional;
	exports com.niton.reactj.api.binding.builder.exposed;
	exports com.niton.reactj.api.binding.builder;
	exports com.niton.reactj.api.binding.predicates;
	exports com.niton.reactj.api.binding.runnable;

	exports com.niton.reactj.api.diff;
	exports com.niton.reactj.api.event;
	exports com.niton.reactj.api.exceptions;
	exports com.niton.reactj.api.mvc;
	exports com.niton.reactj.api.observer;
	exports com.niton.reactj.api.proxy;
	exports com.niton.reactj.api.react;
	exports com.niton.reactj.api.util;


	exports com.niton.reactj.testing.event;
	exports com.niton.reactj.testing.mvc;
	exports com.niton.reactj.testing.observer;
	opens com.niton.reactj.testing.event to org.junit.platform.commons;
	opens com.niton.reactj.testing.mvc to org.junit.platform.commons;
	opens com.niton.reactj.testing.observer to org.junit.platform.commons;
}