package com.niton.reactj.examples.simplelist;

import com.niton.reactj.special.ListView;

import javax.swing.*;
import java.util.function.Function;

public class SwingListView<M> extends ListView<M, JComponent> {
	private DefaultListModel<M> model = new DefaultListModel<>();
	private JPanel view = new JPanel();
	protected SwingListView(Function<M, JComponent> elementCreator) {
		super(elementCreator);
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
	}

	@Override
	public void add(int index,JComponent model) {
		view.add(model,index);
		view.repaint();
		view.validate();
	}

	@Override
	public void remove(JComponent model) {
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
	public JComponent getView() {
		return view;
	}
}
