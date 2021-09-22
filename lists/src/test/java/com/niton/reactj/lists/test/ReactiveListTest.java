package com.niton.reactj.lists.test;

import com.niton.reactj.lists.ReactiveList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ReactiveListTest {
	@Test
	void failConstruct() {
		assertThrows(UnsupportedOperationException.class, ReactiveList::new);
	}
}