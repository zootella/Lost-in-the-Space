package lost.in.the.space.user;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lost.in.the.space.program.Main;
import lost.in.the.space.program.Program;
import lost.in.the.space.program.Snippet;

import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.user.CornerIcon;
import org.zootella.cheat.user.Face;
import org.zootella.cheat.user.Screen;
import org.zootella.cheat.user.widget.BigTextField;
import org.zootella.cheat.user.widget.ClearButton;
import org.zootella.cheat.user.widget.Grip;
import org.zootella.cheat.user.widget.PlaceButton;

/** The main window on the screen. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window(Program program) {
		this.program = program;
		
		Face.blend(); // Tell Java how to show the program's user interface

		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLayout(null);
		
		panel = new MyPanel();
		panel.setLayout(null);
		
		restoreAction = new RestoreAction();
		closeAction = new CloseAction();
		exitAction = new ExitAction();
		browseAction = new BrowseAction();
		
		Grip grip = new Grip(frame, new Rectangle(0, 0, 300, 25));
		
		ClearButton clear = new ClearButton(closeAction, new Rectangle(10, 155, 80, 25));
		
		PlaceButton close = new PlaceButton(closeAction,   new Rectangle(10, 35, 80, 25), Color.black);
		PlaceButton exit = new PlaceButton(exitAction,     new Rectangle(10, 75, 80, 25), Color.black);
		PlaceButton browse = new PlaceButton(browseAction, new Rectangle(10, 115, 80, 25), Color.black);
		
		BigTextField field = new BigTextField(new Rectangle(10, 200, 200, 40), new Font("Helvetica,Arial", Font.PLAIN, 24));
		
		panel.add(grip.label);
		panel.add(close.button);
		panel.add(exit.button);
		panel.add(browse.button);
		panel.add(clear.label);
		panel.add(field.field);
		
		final String iconPath = "lost/in/the/space/icon.gif";
		
		
		
		Dimension d = new Dimension(400, 647);
		
		panel.setSize(d);
		frame.setSize(d);

		frame.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		frame.setIconImage(Face.image(iconPath));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height));
		frame.setContentPane(panel);
		
		
		
		
		icon = new CornerIcon(Main.name, Face.image(iconPath), restoreAction, exitAction);
		
		
		try {
			image = ImageIO.read(new File("skin.png"));
		} catch (IOException e) { throw new DiskException(e); }

		show(true);
	}
	
	public final Program program;

	public final JFrame frame;
	public final JPanel panel;
	public final CornerIcon icon;
	
	private final BufferedImage image;
	
	//TODO move grip in here so the whole window minus child widgets does it
	
	
	private class MyPanel extends JPanel {
		@Override public void paintComponent(Graphics g) {
//			g.drawImage(image, 0, 0, null);
		}
	}
	

	// When the user clicks the main window's corner X, Java calls this windowClosing() method
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {

				close(me());
				
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	private Window me() { return this; }

	@Override public void close() {
		if (already()) return;
		
		close(icon);
		
		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}
	
	
	
	
	
	public void show(boolean b) {
		if (show == b) return;
		show = b;

		frame.setVisible(show);
		icon.show(!show);
	}
	private boolean show;
	
	

	// Action

	private final RestoreAction restoreAction;
	private class RestoreAction extends AbstractAction {
		public RestoreAction() { super("Restore"); } // Text for the button
		public void actionPerformed(ActionEvent a) {
			try {
				
				show(true);

			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final CloseAction closeAction;
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("Close"); } // Text for the button
		public void actionPerformed(ActionEvent a) {
			try {
				
				show(false);

			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	
	private final ExitAction exitAction;
	private class ExitAction extends AbstractAction {
		public ExitAction() { super("Exit"); } // Text for the button
		public void actionPerformed(ActionEvent a) {
			try {
				
				close(program);

			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final BrowseAction browseAction;
	private class BrowseAction extends AbstractAction {
		public BrowseAction() { super("Browse"); } // Text for the button
		public void actionPerformed(ActionEvent a) {
			try {
				
				Snippet.snippet();
				

			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
