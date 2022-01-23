package com.niton.reactj.test.binding;

import com.niton.reactj.api.binding.Binding;
import com.niton.reactj.api.binding.ConditionalBinding;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionalBindingTest {

	@Test
	void testConditionalBinding() {
		AtomicReference<String> ref     = new AtomicReference<>();
		AtomicReference<String> ref2    = new AtomicReference<>();
		var                     base    = new Binding<>(ref2::set, ref::get);
		var                     binding = new ConditionalBinding<>(base, e -> true);
		ref.set("test");
		binding.run();
		assertEquals("test", ref2.get());
		binding.setPredicate(e -> false);
		ref.set("test2");
		binding.run();
		assertEquals(
				"test",
				ref2.get(),
				"When predicate is false, the base binding should not be run"
		);
	}

}