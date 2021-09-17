module reactj.observer {
	requires transitive reactj.utils;
	requires org.junit.jupiter.api;

	exports com.niton.reactj.observer;
	exports com.niton.reactj.observer.testing;

	opens com.niton.reactj.observer.testing to org.junit.jupiter,org.junit.platform.commons;
}