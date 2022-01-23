module reactj.objects {
	requires transitive reactj.api;
	requires org.objenesis;
	requires org.apache.commons.lang3;
	requires jdk.unsupported;
	requires org.slf4j;

	exports com.niton.reactj.objects;
	exports com.niton.reactj.objects.dsl;
	exports com.niton.reactj.objects.proxy;
	exports com.niton.reactj.objects.annotations;
	exports com.niton.reactj.objects.observer;
	exports com.niton.reactj.objects.reflect;
}