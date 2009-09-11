package lost.in.the.space.user;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lost.in.the.space.program.Bridge;
import lost.in.the.space.program.Main;
import lost.in.the.space.program.Program;
import lost.in.the.space.program.Snippet;
import net.roydesign.mac.MRJAdapter;

import org.json.JSONObject;
import org.zootella.cheat.desktop.Desktop;
import org.zootella.cheat.desktop.Open;
import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Once;
import org.zootella.cheat.state.View;
import org.zootella.cheat.user.CornerIcon;
import org.zootella.cheat.user.Face;
import org.zootella.cheat.user.Refresh;
import org.zootella.cheat.user.Screen;
import org.zootella.cheat.user.widget.ClearButton;
import org.zootella.cheat.user.widget.ClearLabel;
import org.zootella.cheat.user.widget.Grip;
import org.zootella.cheat.user.widget.TextField;

/** The main window on the screen. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window(Program program) {
		this.program = program;
		
		Face.blend(); // Tell Java how to show the program's user interface
		
		try {
			image = ImageIO.read(new File(Guide.skin));
		} catch (IOException e) { throw new DiskException(e); }

		restoreAction = new RestoreAction();
		exitAction = new ExitAction();
		closeAction = new CloseAction();
		chooseAction = new ChooseAction();
		openAction = new OpenAction();
		
		String say;
		if (Desktop.hasDock())
			say = "Quit";
		else
			say = "Exit";
		
		exit = new ClearButton(exitAction, Guide.ink, Guide.font, Guide.exit, null, say);
		close = new ClearButton(closeAction, Guide.ink, Guide.font, Guide.close, null, "Close");
		choose = new ClearButton(chooseAction, Guide.ink, Guide.font, Guide.choose, "Folder", "Choose Folder");
		open = new ClearButton(openAction, Guide.ink, Guide.font, Guide.open, null, "Open Folder");
		keywordLabel = new ClearLabel(Guide.ink, Guide.font, Guide.keywordLabel, "Keyword");
		extLabel = new ClearLabel(Guide.ink, Guide.font, Guide.extLabel, "Ext");
		keywordField = new TextField(Guide.ink, Guide.page, Guide.ink, Guide.select, Guide.bigFont, Guide.keyword);
		extField = new TextField(Guide.ink, Guide.page, Guide.ink, Guide.select, Guide.bigFont, Guide.ext);
		status = new ClearLabel(Guide.ink, Guide.font, Guide.status, null);
		
		panel = new MyPanel();
		panel.setLayout(null);
		panel.setSize(Guide.window);
		panel.add(exit.label);
		panel.add(close.label);
		panel.add(choose.label);
		panel.add(open.label);
		panel.add(keywordLabel.label);
		panel.add(extLabel.label);
		panel.add(keywordField.field);
		panel.add(extField.field);
		panel.add(status.label);

		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLayout(null);
		frame.setSize(Guide.window);
		frame.setIconImage(Face.image(Guide.icon));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height));
		frame.setContentPane(panel);

		if (Desktop.hasTray())
			icon = new CornerIcon(Main.name, Face.image(Guide.icon), restoreAction, exitAction);
		else
			icon = null;
		
		new Grip(frame, panel);

		keywordField.field.getDocument().addDocumentListener(new MyDocumentListener());
		extField.field.getDocument().addDocumentListener(new MyDocumentListener());
		frame.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		MRJAdapter.addQuitApplicationListener(new MyQuitActionListener());
		MRJAdapter.addReopenApplicationListener(new MyReopenActionListener());

		// Make our inner View object and connect the Model below to it
		view = new MyView();
		program.core.model.add(view); // When the Model below changes, it will call our view.refresh() method
		view.refresh();

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
	private final ClearLabel keywordLabel;
	private final ClearLabel extLabel;
	private final TextField keywordField;
	private final TextField extField;
	private final ClearLabel status;

	@Override public void close() {
		if (already()) return;
		close(icon);
		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}

	/** true show or false hide the program's window on the screen. */
	public void show(boolean b) {
		if (show == b) return; // Don't hide or show if we're already that way
		show = b;

		frame.setVisible(show);
		if (icon != null) // If we have a tray icon
			icon.show(!show); // Show it in place of the window
	}
	private boolean show; // true when the window is on the screen, false when hidden

	// Event

	/** Java is going to paint the window. */
	private class MyPanel extends JPanel {
		@Override public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null); // Draw the skin background
		}
	}

	/** The user typed in the keyword or ext boxes. */
	private class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) { update(); }
		public void removeUpdate(DocumentEvent e) { update(); }
		public void changedUpdate(DocumentEvent e) {}
	}
	private void update() {
		program.core.enter(keywordField.field.getText(), extField.field.getText()); // Tell the core
	}
	
	// Windows and Mac

	/** On Windows, the user right-clicked the taskbar button and clicked "X Close" or keyed Alt+F4. */
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {
				show(false);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	/** On Mac, the user clicked the Quit menu item from the top left of the screen or from the program's icon on the dock. */
	private class MyQuitActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				close(program); // Close the program
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	/** On Mac, the user clicked the program's icon on the dock. */
	private class MyReopenActionListener implements ActionListener {
		@Override public void actionPerformed(ActionEvent a) {
			try {
				show(true); // If the window was hidden, put it back on the screen
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	// Action

	/** Show the window on the screen. */
	private final RestoreAction restoreAction;
	private class RestoreAction extends AbstractAction {
		public RestoreAction() { super("Restore"); } // Text for the user
		public void actionPerformed(ActionEvent a) {
			try {
				show(true);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	/** Close the program. */
	private final ExitAction exitAction;
	private class ExitAction extends AbstractAction {
		public ExitAction() { super("Exit"); }
		public void actionPerformed(ActionEvent a) {
			try {
				close(program);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	/** Hide the window, but keep the program running. */
	private final CloseAction closeAction;
	private class CloseAction extends AbstractAction {
		public CloseAction() { super("Close"); }
		public void actionPerformed(ActionEvent a) {
			try {
				show(false);
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	/** Choose the shared folder. */
	private final ChooseAction chooseAction;
	private class ChooseAction extends AbstractAction {
		public ChooseAction() { super("Choose"); }
		public void actionPerformed(ActionEvent a) {
			try {

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Have it limit the choice to just files or just folders
				int result = chooser.showOpenDialog(frame); // Control sticks here while the user is deciding
				if (result != JFileChooser.APPROVE_OPTION)
					return; // The user pressed Cancel
				
				String s = chooser.getSelectedFile().getAbsolutePath();
				String message = program.core.share(s);
				if (message != null)
					JOptionPane.showMessageDialog(frame, message, Main.name, JOptionPane.WARNING_MESSAGE);

			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	

	/** Open the shared folder. */
	private final OpenAction openAction;
	private class OpenAction extends AbstractAction {
		public OpenAction() { super("Open"); }
		public void actionPerformed(ActionEvent a) {
			try {

				//TODO test a download right here
				
				if (downloadOnce.once()) {
					
					JSONObject p = new JSONObject();
					p.put("guid", keywordField.field.getText());
					p.put("hash", extField.field.getText());
					p.put("path", program.core.share().add("downloaded.mp3").toString());
					
					JSONObject o = new JSONObject();
					o.put("download", p);
					
					program.bridge.sendDown(o);
					
				} else {

					program.bridge.sendDown(Bridge.say("progress"));
				}

				/*
				Open.file(program.core.share());
				*/
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	private final Once downloadOnce = new Once();

	// View

	// When our Model underneath changes, it calls these methods
	private final View view;
	private class MyView implements View {

		// The Model beneath changed, we need to update what we show the user
		public void refresh() {
			Refresh.text(status.label, program.core.model.status());
			Refresh.text(open.label, program.core.model.share());
		}

		// The Model beneath closed, take this View off the screen
		public void vanish() { close(me()); }
	}
	
	/** Give inner classes a link to this outer object. */
	private Window me() { return this; }
}
