package com.niton.reactj.examples.simplelist;

import com.niton.reactj.special.ListView;

import javax.swing.*;
import java.util.function.Function;

public class SwingListView<M> extends ListView<M, JLabel,JPanel> {
	private final DefaultListModel<M> model = new DefaultListModel<>();
	private final JPanel              view  = new JPanel();
	protected SwingListView(Function<M, JLabel> elementCreator) {
		super(elementCreator);
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
	}

	@Override
	public void add(int index,JLabel model) {
		view.add(model,index);
		view.repaint();
		view.validate();
	}

	@Override
	public void remove(JLabel model) {
		view.remove(model);
		view.repaint();
		view.validate();
	}

	@Override
	public void remove(int index) {
		view.remove(index);
		view.repaint();
		view.validate();
	}

	@Override
	public int size() {
		return view.getComponentCount();
	}

	@Override
	public JPanel getView() {
		return view;
	}
}
