module reactj.observer {
	requires transitive reactj.utils;

	requires static org.junit.jupiter.api;
	opens com.niton.reactj.testing.observer to org.junit.platform.commons;

	exports com.niton.reactj.testing.observer;
	exports com.niton.reactj.api.observer;
}