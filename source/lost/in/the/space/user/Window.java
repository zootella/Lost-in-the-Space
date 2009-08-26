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
import javax.swing.JLabel;
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
import org.zootella.cheat.user.widget.ClearLabel;
import org.zootella.cheat.user.widget.Grippy;
import org.zootella.cheat.user.widget.PlaceButton;
import org.zootella.cheat.user.widget.WhiteLabel;

/** The main window on the screen. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window(Program program) {
		this.program = program;
		
		Face.blend(); // Tell Java how to show the program's user interface

		
		panel = new MyPanel();
		panel.setLayout(null);
		panel.setSize(Guide.window);
		
		
		restoreAction = new RestoreAction();
		exitAction = new ExitAction();
		closeAction = new CloseAction();
		chooseAction = new ChooseAction();
		openAction = new OpenAction();
		
		/*
		status = new ClearLabel(new Rectangle(10, 300, 200, 14));
		
		status.label.setText("hello");
		
		
		
		
		ClearButton clear = new ClearButton(closeAction, new Rectangle(10, 155, 80, 25));
		
		PlaceButton close = new PlaceButton(closeAction,   new Rectangle(10, 35, 80, 25), Color.black);
		PlaceButton exit = new PlaceButton(exitAction,     new Rectangle(10, 75, 80, 25), Color.black);
		PlaceButton browse = new PlaceButton(browseAction, new Rectangle(10, 115, 80, 25), Color.black);
		
		BigTextField field = new BigTextField(new Rectangle(10, 200, 200, 40), new Font("Helvetica,Arial", Font.PLAIN, 24));
		
		panel.add(status.label);
		panel.add(close.button);
		panel.add(exit.button);
		panel.add(browse.button);
		panel.add(clear.label);
		panel.add(field.field);
		*/
		
		
		
		
		exit = new ClearButton(exitAction, Guide.exit, null, "Exit");
		close = new ClearButton(closeAction, Guide.close, null, "Close");
		choose = new ClearButton(chooseAction, Guide.choose, "Shared", "Choose Folder");
		open = new ClearButton(openAction, Guide.open, null, null);
		name = new BigTextField(Guide.name, Guide.big);
		ext = new BigTextField(Guide.ext, Guide.big);
		status = new ClearLabel(Guide.status);
		
		panel.add(exit.label);
		panel.add(close.label);
		panel.add(choose.label);
		panel.add(open.label);
		panel.add(name.field);
		panel.add(ext.field);
		panel.add(status.label);
		
		
		
		
		
		
		
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLayout(null);
		
		frame.setSize(Guide.window);

		frame.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		frame.setIconImage(Face.image(Guide.icon));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height));
		frame.setContentPane(panel);
		
		
		
		
		icon = new CornerIcon(Main.name, Face.image(Guide.icon), restoreAction, exitAction);
		
		new Grippy(frame, panel);
		
		try {
			image = ImageIO.read(new File(Guide.skin));
		} catch (IOException e) { throw new DiskException(e); }

		show(true);
	}
	
	public final Program program;

	public final JFrame frame;
	public final JPanel panel;
	public final CornerIcon icon;
	private final BufferedImage image;
	
	private final ClearButton exit;
	private final ClearButton close;
	private final ClearButton choose;
	private final ClearButton open;
	private final BigTextField name;
	private final BigTextField ext;
	private final ClearLabel status;
	
	
	
	private class MyPanel extends JPanel {
		@Override public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
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
		public RestoreAction() { super("Restore"); } // Text for the user
		public void actionPerformed(ActionEvent a) {
			try {
				show(true);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final ExitAction exitAction;
	private class ExitAction extends AbstractAction {
		public ExitAction() { super("Exit"); }
		public void actionPerformed(ActionEvent a) {
			try {
				close(program);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final CloseAction closeAction;
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("Close"); }
		public void actionPerformed(ActionEvent a) {
			try {
				show(false);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final ChooseAction chooseAction;
	private class ChooseAction extends AbstractAction {
		public ChooseAction() { super("Browse"); }
		public void actionPerformed(ActionEvent a) {
			try {
				Snippet.snippet();
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	private final OpenAction openAction;
	private class OpenAction extends AbstractAction {
		public OpenAction() { super("Open"); }
		public void actionPerformed(ActionEvent a) {
			try {
				Snippet.snippet();
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
}
