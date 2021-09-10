package com.niton.reactj.examples.simplelist;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class PrettyTextView extends JPanel {
	private JLabel max,small,color;
	public PrettyTextView(String text){
		max = new JLabel(text);
		max.setFont(max.getFont().deriveFont(Font.BOLD, 26f));
		small = new JLabel(text);
		color = new JLabel(text);
		color.setForeground(Color.GREEN);

		setLayout(new GridLayout());
		setMaximumSize(new Dimension(350,max.getPreferredSize().height));
		add(max);
		add(small);
		add(color);
	}
}
