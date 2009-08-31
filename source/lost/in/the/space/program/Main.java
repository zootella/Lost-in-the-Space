package lost.in.the.space.program;

import javax.swing.SwingUtilities;

import lost.in.the.space.bridge.Start;

import org.zootella.cheat.process.Mistake;

public class Main {
	
	/** The name of this program */
	public static final String name = "Lost in the Space";
    
    public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() { // Have the normal Swing thread call this run() method
			public void run() {
				try {

					// Put the window on the screen
					new Program();

				} catch (Exception e) { Mistake.stop(e); } // Stop the program for an Exception we didn't expect
			}
		});

		// Make and start LimeWire's Gnutella core underneath
		Start.main();
    }
}
