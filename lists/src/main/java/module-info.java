module reactj.lists {
	requires reactj.api;
	requires static org.junit.jupiter.api;

	exports com.niton.reactj.api.lists;
	exports com.niton.reactj.lists.diff;
	exports com.niton.reactj.lists.observer;
	exports com.niton.reactj.lists.proxy;
}