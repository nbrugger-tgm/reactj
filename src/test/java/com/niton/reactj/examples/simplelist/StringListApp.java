package com.niton.reactj.examples.simplelist;

import com.niton.reactj.special.ReactiveList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StringListApp {
	public static void main(String[] args) throws InterruptedException {
		ReactiveList<String> someArray = ReactiveList.create(new ArrayList<>());

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout());

		SwingListView<String> view = new SwingListView<>(
			(s) -> new ClickableListEntry(s, someArray::remove)
		);
		view.setList(someArray);

		frame.add(view.getView());
		frame.pack();
		frame.setVisible(true);

		//Modifiying List to see change
		int i = 0;
		while(true) {
			Thread.sleep(1000);
			someArray.add(Integer.toString(i++));
		}
	}
}
