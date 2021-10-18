module reactj.observer {
	requires transitive reactj.utils;
	requires transitive net.bytebuddy;

	requires static org.junit.jupiter.api;
	opens com.niton.reactj.testing.observer to org.junit.platform.commons;

	exports com.niton.reactj.api.observer;
	exports com.niton.reactj.api.proxy;
	exports com.niton.reactj.api.react;

	exports com.niton.reactj.observer.infusion;
	exports com.niton.reactj.observer.util;
	exports com.niton.reactj.proxy;

	exports com.niton.reactj.testing.observer;
}