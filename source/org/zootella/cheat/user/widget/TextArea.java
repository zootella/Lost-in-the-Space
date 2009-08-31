package org.zootella.cheat.user.widget;

import javax.swing.JTextArea;

import org.zootella.cheat.user.Face;


public class TextArea {
	
	public TextArea() {
		area = new JTextArea();
		area.setFont(Face.font());
		new TextMenu(area);
	}
	
	public final JTextArea area;
}
