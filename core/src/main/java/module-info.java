module reactj.core {
	exports com.niton.reactj.api;
	exports com.niton.reactj.api.annotation;
	exports com.niton.reactj.api.proxy;
	exports com.niton.reactj.api.mvc;
	exports com.niton.reactj.api.exceptions;
	exports com.niton.reactj.api.observer;
	requires transitive reactj.observer;
	requires transitive reactj.utils;
	requires java.se;
	requires org.apache.commons.lang3;
	requires transitive javasisst;
}