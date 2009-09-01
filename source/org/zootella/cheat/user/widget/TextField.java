package org.zootella.cheat.user.widget;

import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextField {
	
	public TextField(Font font, Rectangle place) {
		field = new JTextField();
		field.setLayout(null);
		field.setBounds(place);
		field.setFont(font);
		field.setBorder(BorderFactory.createEmptyBorder());

		new TextMenu(field);
	}
	
	public final JTextField field;
}
