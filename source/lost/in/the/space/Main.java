package lost.in.the.space;

import javax.swing.SwingUtilities;

import org.zootella.cheat.process.Mistake;

public class Main {
	
	public static final String name = "Lost in the Space";
    
    public static void main(String[] args) {
    	
    	System.out.println("before");
    	Start.main();
    	System.out.println("after");
    	
    	
    	
    	
    	// make a window here, then add a working exit button to it
    	
		SwingUtilities.invokeLater(new Runnable() { // Have the normal Swing thread call this run() method
			public void run() {
				try {

					// Make and start the program
					new Window();

				} catch (Exception e) { Mistake.stop(e); } // Stop the program for an Exception we didn't expect
			}
		});
    	
    	
    }
}
