package lost.in.the.space.program;

import lost.in.the.space.user.Window;

import org.json.JSONObject;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

/** The Program object represents the whole running program. */
public class Program extends Close { // This class has a close() method we have to call before the program quits
	
	// Object

	/** Start the program. */
	public Program() {
		update = new Update(new MyReceive());
		update.send();
		bridge = Bridge.instance(); // Connect to the Bridge that lets us talk to the LimeWire API below
		bridge.updateUp(update); // We want to find out when messages come up from the LimeWire API
		
		this.core = new Core(this); // First, make the Core of the program
		this.window = new Window(this); // Then, put the Window on the screen
	}

	/** The Window on the screen that interacts with the user. */
	public final Window window;
	/** The Core of this program that does everything. */
	public final Core core;
	/** Our Bridge to the LimeWire API below. */
	public final Bridge bridge;
	
	/** This object's Update object which other objects use to tell us when something we care about has changed. */
	private final Update update;

	/** Close the program to prepare for the Java process to exit. */
	@Override public void close() {
		if (already()) return; // Only go in here once
		close(window); // Close the objects that are a part of us
		close(core);
		Mistake.closeCheck(); // Count that every object that needed to be closed got closed
		bridge.sendDown(Bridge.say("quit")); // Tell the LimeWire API below to exit
	}
	
	// Messages

	/** The LimeWire API below sent a new message up for us. */
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			while (true) { // Loop for each message that has arrived from the core below
				JSONObject r = bridge.receiveUp();
				if (r == null)
					return; // No more messages right now
				
				message(r); // Do the message
			}
		}
	}

	/** Look at message r from the LimeWire API below. */
	public void message(JSONObject r) {
		
		if      (r.has("loaded"))   core.loaded();    // The API finished loading, tell our Core
		else if (r.has("result"))   core.result(r);   // The API got a search result, tell our Core
		else if (r.has("progress")) core.progress(r); // Download progress information, tell our Core
		else if (r.has("quitted"))  System.exit(0);   // The API finished shutting down, exit the Java process
	}
}
