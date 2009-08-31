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

import net.roydesign.mac.MRJAdapter;

import org.zootella.cheat.desktop.Desktop;
import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.user.CornerIcon;
import org.zootella.cheat.user.Face;
import org.zootella.cheat.user.Screen;
import org.zootella.cheat.user.widget.BigTextField;
import org.zootella.cheat.user.widget.ClearButton;
import org.zootella.cheat.user.widget.ClearLabel;
import org.zootella.cheat.user.widget.Grip;
import org.zootella.cheat.user.widget.PlaceButton;
import org.zootella.cheat.user.widget.WhiteLabel;



import java.awt.event.ActionListener;

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
		
		String exitDesktop;
		if (Desktop.hasDock())
			exitDesktop = "Quit";
		else
			exitDesktop = "Exit";
		
		exit = new ClearButton(exitAction, Guide.font, Guide.exit, null, exitDesktop);
		close = new ClearButton(closeAction, Guide.font, Guide.close, null, "Close");
		choose = new ClearButton(chooseAction, Guide.font, Guide.choose, "Shared", "Choose Folder");
		open = new ClearButton(openAction, Guide.font, Guide.open, null, "Open Folder");
		
		nameLabel = new ClearLabel(Guide.font, Guide.nameLabel, "Keyword");
		extLabel = new ClearLabel(Guide.font, Guide.extLabel, "Ext");
		
		name = new BigTextField(Guide.bigFont, Guide.name);
		ext = new BigTextField(Guide.bigFont, Guide.ext);
		
		status = new ClearLabel(Guide.font, Guide.status, null);
		
		panel.add(exit.label);
		panel.add(close.label);
		panel.add(choose.label);
		panel.add(open.label);
		panel.add(nameLabel.label);
		panel.add(extLabel.label);
		panel.add(name.field);
		panel.add(ext.field);
		panel.add(status.label);
		
		
		status.label.setText("This is the status");
		open.label.setText("This is the path");
		
		
		
		
		
		
		
		
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
		
		MRJAdapter.addQuitApplicationListener(new MyQuitActionListener());
		MRJAdapter.addReopenApplicationListener(new MyReopenActionListener());
		
		
		if (Desktop.hasTray())
			icon = new CornerIcon(Main.name, Face.image(Guide.icon), restoreAction, exitAction);
		else
			icon = null;
		
		new Grip(frame, panel);
		
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
	private final ClearLabel nameLabel;
	private final ClearLabel extLabel;
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
	
	// Runs when the Mac user clicks the Quit menu item from the top left or from the dock
	private class MyQuitActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				close(me());
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	private class MyReopenActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				show(true);
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
		if (icon != null)
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
