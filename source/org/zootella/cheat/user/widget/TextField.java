package org.zootella.cheat.user.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextField {
	
	public TextField(Color ink, Color select, Color selectInk, Font font, Rectangle place) {
		field = new JTextField();
		field.setLayout(null);
		field.setBounds(place);
		field.setForeground(ink);
		field.setSelectionColor(select);
		field.setSelectedTextColor(selectInk);
		field.setFont(font);
		field.setBorder(BorderFactory.createEmptyBorder());
		

		new TextMenu(field);
	}
	
	public final JTextField field;
}
