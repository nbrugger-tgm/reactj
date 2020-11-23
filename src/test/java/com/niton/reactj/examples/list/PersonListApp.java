package com.niton.reactj.examples.list;


import com.niton.reactj.special.ReactiveList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PersonListApp {
	public static void main(String[] args) {
		ReactiveList<Person,String> list = ReactiveList.create(new ArrayList<>());

		PersonList view = new PersonList(new PersonListController(list));
		view.setData(list);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout());
		frame.add(view.getView());
		frame.pack();
		frame.setVisible(true);


	}
}
