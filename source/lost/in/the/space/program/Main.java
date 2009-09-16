package lost.in.the.space.program;

import javax.swing.SwingUtilities;

import lost.in.the.space.bridge.Start;

import org.zootella.cheat.process.Mistake;

/** When the program starts, Java begins by calling this main() method. */
public class Main {
	
	/** The name of this program. */
	public static final String name = "Lost in the Space"; // Named after http://javieralcalde.deviantart.com/art/Lost-in-the-Space-125460912
    
	/** Java starts running the program here. */
    public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() { // Have the Swing event thread call this run() method
			public void run() {
				try {

					// Make the Program object which starts the program and puts the window on the screen
					new Program();

				} catch (Exception e) { Mistake.stop(e); } // Stop the program for an exception we don't expect
			}
		});

		// Make and start LimeWire's Gnutella API underneath
		Start.main(); // This happens at the same time, but takes longer
    }
}
