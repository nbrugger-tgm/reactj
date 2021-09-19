module reactj.core.test {
	requires java.desktop;
	requires reactj.core;
	requires static org.junit.jupiter.api;
	opens com.niton.reactj.api.test to org.junit.jupiter,org.junit.platform.commons,reactj.core;
	opens com.niton.reactj.api.examples to reactj.core;
	opens com.niton.reactj.api.examples.CLI to reactj.core;
	opens com.niton.reactj.api.examples.superbinding to reactj.core;
	opens com.niton.reactj.api.examples.swing to reactj.core;
}