module reactj.lists {
	requires reactj.utils;
	requires static org.junit.jupiter.api;
	requires reactj.observer;

	exports com.niton.reactj.api.lists;
	exports com.niton.reactj.lists.diff;
	exports com.niton.reactj.lists.observer;
	exports com.niton.reactj.lists.proxy;

	opens com.niton.reactj.lists.proxy to reactj.observer;
}