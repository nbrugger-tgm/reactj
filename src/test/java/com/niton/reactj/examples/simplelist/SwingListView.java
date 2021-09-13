package com.niton.reactj.examples.simplelist;

import com.niton.reactj.special.ListView;

import javax.swing.*;
import java.util.function.Function;

public class SwingListView<M> extends ListView<M, JComponent, JComponent> {
	private final JPanel              view  = new JPanel();
	private final JScrollPane scrollPane = new JScrollPane(view);

	protected SwingListView(Function<M, JComponent> elementCreator) {
		super(elementCreator);
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
	}

	@Override
	public void add(int index, JComponent model) {
		view.add(model, index);
		view.repaint();
		scrollPane.validate();
	}

	@Override
	public void remove(JComponent model) {
		view.remove(model);
		view.repaint();
		scrollPane.validate();
	}

	@Override
	public void remove(int index) {
		view.remove(index);
		view.repaint();
		scrollPane.validate();
	}

	@Override
	public int size() {
		return view.getComponentCount();
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}
}
