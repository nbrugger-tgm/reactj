package com.niton.reactj.examples.listobserver;

import com.niton.reactj.observers.ListObserver;

import java.util.ArrayList;
import java.util.List;

public class ListObserverExample {
	public static void main(String[] args) {
		List<String> someList = new ArrayList<>();
		ListObserver<String> observer = new ListObserver<>();
	}
}
