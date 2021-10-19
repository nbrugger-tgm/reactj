module reactj.utils {
	requires static org.junit.jupiter.api;
	opens com.niton.reactj.testing.event to org.junit.platform.commons;

	exports com.niton.reactj.api.event;
	exports com.niton.reactj.api.diff;
	exports com.niton.reactj.api.exceptions;

	exports com.niton.reactj.utils.reflections;
	exports com.niton.reactj.utils.exceptions;

	exports com.niton.reactj.testing.event;
}