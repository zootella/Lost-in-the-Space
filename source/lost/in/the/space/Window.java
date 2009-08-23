package lost.in.the.space;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.user.Screen;

/** The main window on the screen. */
public class Window extends Close {

	// Object

	/** Make the program's main window on the screen. */
	public Window() {

		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.setLayout(null);
		
		panel = new JPanel();
		panel.setLayout(null);
		
		Dimension d = new Dimension(300, 600);
		
		panel.setSize(d);
		frame.setSize(d);

		frame.addWindowListener(new MyWindowListener()); // Have Java tell us when the user closes the window
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("lost/in/the/space/icon.gif")));
		frame.setTitle(Main.name);
		frame.setBounds(Screen.positionSize(frame.getSize().width, frame.getSize().height));
		frame.setContentPane(panel);
		frame.setVisible(true);
	}

	public final JFrame frame;
	public final JPanel panel;

	// When the user clicks the main window's corner X, Java calls this windowClosing() method
	private class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent w) {
			try {

				frame.setVisible(false);
				
			} catch (Exception e) { Mistake.stop(e); }
		}
	}

	@Override public void close() {
		if (already()) return;
		
		frame.setVisible(false);
		frame.dispose(); // Dispose the frame so the process can close
	}
}
