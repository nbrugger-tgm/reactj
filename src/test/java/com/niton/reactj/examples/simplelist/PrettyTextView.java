package com.niton.reactj.examples.simplelist;

import javax.swing.*;
import java.awt.*;

public class PrettyTextView extends JPanel {
	public PrettyTextView(String text){
		JLabel max = new JLabel(text);
		max.setFont(max.getFont().deriveFont(Font.BOLD, 26f));
		JLabel small = new JLabel(text);
		JLabel color = new JLabel(text);
		color.setForeground(Color.GREEN);

		setLayout(new GridLayout());
		setMaximumSize(new Dimension(350, max.getPreferredSize().height));
		add(max);
		add(small);
		add(color);
	}
}
