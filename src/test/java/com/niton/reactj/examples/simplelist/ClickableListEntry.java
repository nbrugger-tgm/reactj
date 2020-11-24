package com.niton.reactj.examples.simplelist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickableListEntry extends JLabel {
	private final Remover remover;
	public ClickableListEntry(String text, Remover remover) {
		super(text);
		this.remover = remover;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1){
					remover.remove(getText());
				}else{
					Color oldColor = getForeground();
					setForeground(new Color(100-oldColor.getRed(),255-oldColor.getGreen(),255-oldColor.getBlue()));
				}
			}
		});
	}
	@FunctionalInterface
	public interface Remover{
		void remove(String s);
	}
}
