package lost.in.the.space.user;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lost.in.the.space.program.Main;
import lost.in.the.space.program.Program;
import net.roydesign.mac.MRJAdapter;

import org.zootella.cheat.desktop.Desktop;
import org.zootella.cheat.desktop.Open;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.View;
import org.zootella.cheat.user.CornerIcon;
import org.zootella.cheat.user.Face;
import org.zootella.cheat.user.Refresh;
import org.zootella.cheat.user.Screen;
import org.zootella.cheat.user.skin.Skin;
import org.zootella.cheat.user.widget.ClearButton;
import org.zootella.cheat.user.widget.ClearLabel;
import org.zootella.cheat.user.widget.Grip;
import org.zootella.cheat.user.widget.TextField;

/** The main window on the screen, and Action objects Java calls when the user clicks buttons. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window(Program program) {
		this.program = program; // Save the given link back up to the Program object
		
		Face.blend(); // Tell Java how to show the program's user interface

		skin = new Skin(Path.work(Guide.skin), Guide.window); // Load the Skin image
		Color ink = skin.color(Guide.ink); // Eyedropper in colors for the text
		Color typeInk = skin.color(Guide.typeInk);
		Color typePage = skin.color(Guide.typePage);
		Color selectInk = skin.color(Guide.selectInk);
		Color selectPage = skin.color(Guide.selectPage);

		restoreAction = new RestoreAction(); // Make Action objects that Java calls when the user clicks
		exitAction = new ExitAction();
		closeAction = new CloseAction();
		chooseAction = new ChooseAction();
		openAction = new OpenAction();
		
		String say;
		if (Desktop.hasDock())
			say = "Quit"; // On Mac, it's called "Quit"
		else
			say = "Exit"; // Windows users are more familiar with "Exit"
		
		exit = new ClearButton(exitAction, ink, Guide.font, Guide.exit, null, say); // Make widgets for the Window
		close = new ClearButton(closeAction, ink, Guide.font, Guide.close, null, "Close");
		choose = new ClearButton(chooseAction, ink, Guide.font, Guide.choose, "Folder", "Choose Folder");
		open = new ClearButton(openAction, ink, Guide.font, Guide.open, null, "Open Folder");
		keywordLabel = new ClearLabel(ink, Guide.font, Guide.keywordLabel, "Keyword");
		extLabel = new ClearLabel(ink, Guide.font, Guide.extLabel, "Ext");
		keywordField = new TextField(typeInk, typePage, selectInk, selectPage, Guide.bigFont, Guide.keyword);
		extField = new TextField(typeInk, typePage, selectInk, selectPage, Guide.bigFont, Guide.ext);
		status = new ClearLabel(ink, Guide.font, Guide.status, null);
		
		panel = new MyPanel(); // The inside of the window that contains all the widgets
		panel.setLayout(null); // No crazy automatic stretching, please
		panel.setSize(Guide.window);
		panel.add(exit.label); // Add all the widgets we made
		panel.add(close.label);
		panel.add(choose.label);
		panel.add(open.label);
		panel.add(keywordLabel.label);
		panel.add(extLabel.label);
		panel.add(keywordField.field);
		panel.add(extField.field);
		panel.add(status.label);

		frame = new JFrame(); // The window on the user's desktop
		frame.setUndecorated(true); // No operating system painted top bar or borders, please
		frame.setResizable(false); // No stretching
		frame.setLayout(null); // We'll position things ourselves
		frame.setSize(Guide.window);
		frame.setIconImage(skin.image(Guide.icon)); // Icon shows up on the Windows taskbar button
		frame.setTitle(Main.name); // Title text also shows up on the Windows taskbar button
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height)); // Pick a random spot on the screen
		frame.setContentPane(panel); // Add all the insides we just set up above

		if (Desktop.hasTray())
			icon = new CornerIcon(Main.name, skin.image(Guide.icon), restoreAction, exitAction);
		else
			icon = null; // On Mac, the dock icon is good enough
		
		new Grip(frame, panel); // Let the user drag the window around the screen all WinAmp-style

		keywordField.field.getDocument().addDocumentListener(new MyDocumentListener()); // Notice when the user types
		extField.field.getDocument().addDocumentListener(new MyDocumentListener());
		
		frame.addWindowListener(new MyWindowListener()); // Find out when the user closes the window from the taskbar
		if (Desktop.hasDock()) {
			MRJAdapter.addQuitApplicationListener(new MyQuitActionListener()); // And from the Mac application menu
			MRJAdapter.addReopenApplicationListener(new MyReopenActionListener()); // And when she clicks the dock icon
		}

		view = new MyView(); // Make our inner View object and connect the Model below to it
		program.core.model.add(view); // When the Model below changes, it will call our view.refresh() method
		view.refresh();

		show(true); // Show the window on the screen
	}
	
	/** A link back up to the Program object we're a part of. */
	public final Program program;

	private final Skin skin;
	public final CornerIcon icon;
	public final JFrame frame;
	public final JPanel panel;
	
	private final ClearButton exit;
	private final ClearButton close;
	private final ClearButton choose;
	private final ClearButton open;
	private final ClearLabel keywordLabel;
	private final ClearLabel extLabel;
	private final TextField keywordField;
	private final TextField extField;
	private final ClearLabel status;

	/** Free all user interface resources when the program closes. */
	@Override public void close() {
		if (already()) return; // Only let the computer past here once
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
	/** true when the window is on the screen, false when it's hidden. */
	private boolean show;

	// Event

	/** Java is going to paint the window. */
	private class MyPanel extends JPanel {
		@Override public void paintComponent(Graphics g) {
			g.drawImage(skin.image, 0, 0, null); // Draw the skin background
		}
	}

	/** The user typed in the keyword or ext boxes. */
	private class MyDocumentListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) { update(); }
		public void removeUpdate(DocumentEvent e) { update(); }
		public void changedUpdate(DocumentEvent e) {}
	}
	private void update() {
		program.core.enter(keywordField.field.getText(), extField.field.getText()); // Tell the Core what the user typed
	}
	
	// Windows and Mac

	/** On Windows, the user right-clicked the taskbar button and clicked "X Close" or keyed Alt+F4. */
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {
				show(false); // Hide the window
			} catch (Exception e) { Mistake.stop(e); } // Stop the program if an Exception we didn't expect happens
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
				String message = program.core.share(s); // Give the path text to the Core
				if (message != null) // If it didn't like it, show the message text it gave us to the user
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
				Open.file(program.core.share()); // Pop open Windows Explorer or the Mac Finder
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	// View

	/** When the Core's Model underneath changes, it calls these methods. */
	private final View view;
	private class MyView implements View {

		/** The Model beneath changed, we need to update what we show the user. */
		public void refresh() {
			Refresh.text(open.label, program.core.model.share());
			Refresh.text(status.label, program.core.model.status());
		}

		/** The Model beneath closed, take this View off the screen. */
		public void vanish() { close(me()); }
	}
	
	/** Give code in inner classes a link to this outer object. */
	private Window me() { return this; }
}
