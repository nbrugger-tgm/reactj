package com.niton.reactj.test;

import com.niton.reactj.special.ListView;
import com.niton.reactj.special.ReactiveList;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {
	public static class MyListComponent extends ListView<Integer,String,List<String>> {
		public final List<String> view = new ArrayList<>();

		protected MyListComponent(Function<Integer, String> elementCreator) {
			super(elementCreator);
		}

		@Override
		public void remove(int index) {
			view.remove(index);
		}

		@Override
		public void remove(String model) {
			view.remove(model);
		}

		@Override
		public int size() {
			return view.size();
		}

		@Override
		public void add(int index, String model) {
			view.add(index,model);
		}

		@Override
		public List<String> getView() {
			return view;
		}
	}

	@Test
	void useCase(){
		ReactiveList<Integer> test = ReactiveList.create(new ArrayList<>());
		test.add(1);
		test.add(2);
		test.add(97);
		MyListComponent view = new MyListComponent(Objects::toString);
		List<String> stringList = view.getView();
		view.setList(test);
		assertTrue(test.stream().map(String::valueOf).allMatch(stringList::contains));
		test.add(9);
		assertEquals(4,stringList.size());
		assertEquals("9",stringList.get(stringList.size()-1));
		test.clear();
		assertEquals(0,stringList.size());
	}
}
