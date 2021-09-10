package com.niton.reactj.examples.simplelist;

import com.niton.reactj.mvc.EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@FunctionalInterface
interface Remover {
	void remove(String s);
}

public class ClickableListEntry extends JLabel {
	public final EventManager<String> onRemove = new EventManager<>();
	public ClickableListEntry(String text) {
		super(text);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					onRemove.fire(getText());
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