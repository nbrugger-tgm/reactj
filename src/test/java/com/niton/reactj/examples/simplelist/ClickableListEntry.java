package com.niton.reactj.examples.simplelist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@FunctionalInterface
interface Remover {
	void remove(String s);
}

public class ClickableListEntry extends JLabel {
	public ClickableListEntry(String text, Remover remover) {
		super(text);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					remover.remove(getText());
				} else {
					Color oldColor = getForeground();
					setForeground(new Color(100 - oldColor.getRed(),
					                        255 - oldColor.getGreen(),
					                        255 - oldColor.getBlue()));
				}
			}
		});
	}


}