package com.niton.reactj.examples.simplelist;

import com.niton.reactj.examples.list.Person;
import com.niton.reactj.observers.ObjectObserver;
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

		SwingListView<String> clickableView = new SwingListView<>(
			(s) -> {
				ClickableListEntry entry = new ClickableListEntry(s);
				entry.onRemove.listen(someArray::remove);
				return entry;
			}
		);
		clickableView.setList(someArray);

		SwingListView<String> normalView = new SwingListView<>(JLabel::new);
		normalView.setList(someArray);

		SwingListView<String> customView = new SwingListView<>(PrettyTextView::new);
		customView.setList(someArray);

		frame.add(clickableView.getView());
		frame.add(normalView.getView());
		frame.add(customView.getView());
		frame.pack();
		frame.setVisible(true);

		//Modifiying List to see change
		int i = 0;
		while(i<100) {
			Thread.sleep(1000);
			someArray.add("Entry "+i++);
		}
	}
}
