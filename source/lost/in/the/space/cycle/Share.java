package lost.in.the.space.cycle;

import java.util.List;

import lost.in.the.space.program.Bridge;

import org.json.JSONObject;
import org.zootella.cheat.exception.ProgramException;
import org.zootella.cheat.file.ListTask;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;
import org.zootella.cheat.time.Egg;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;

/** A Share object shares all the files in a folder. */
public class Share extends Close {

	/** Make a new Share object to share all the files in folder. */
	public Share(Bridge bridge, Path folder) {
		this.bridge = bridge; // Save the given objects
		this.folder = folder;
		
		Receive receive = new MyReceive(); // Make a receive() method that runs when stuff is changed
		update = new Update(receive);
		update.send(); // Run it soon
		
		egg = new Egg(receive, 20 * Time.second); // Set an Egg timer to go off in 20 seconds
		pulse = new Pulse(receive); // Get called to notice when this has happened
	}

	/** A link to the Bridge that lets us talk to the LimeWire API below. */
	private final Bridge bridge;
	
	/** The folder we're sharing. */
	private final Path folder;
	
	/** This object's Update which gets told when something we care about may be changed. */
	private final Update update;
	/** A Pulse which calls receive() so we notice when egg goes off. */
	private final Pulse pulse;
	/** An Egg timer which makes sure this whole process doesn't take more than 20 seconds. */
	private final Egg egg;

	/** Our ListTask which has a Task thread access the disk to list all the files in folder. */
	private ListTask task;
	/** The list of file Name objects task gave us, null before it's done. */
	private List<Name> list;

	/** The ProgramException that kept us from success. */
	public ProgramException exception() { return exception; }
	private ProgramException exception;

	/** Close objects inside, free resources, and don't change again. */
	@Override public void close() {
		if (already()) return;
		close(task);
		close(egg);
		close(pulse);
	}

	/** Something might be new for us to find out about. */
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return;
			try {

				egg.check(); // Make sure this whole thing doesn't take too long
				
				if (no(task))
					task = new ListTask(update, folder); // Have a thread look at the disk
				if (done(task))
					list = task.result(); // Get the result or throw an exception
				
				if (list != null) { // We have the list
					for (Name name : list) { // Loop through it
						JSONObject o = new JSONObject();
						o.put("share", folder.add(name).toString());
						bridge.sendDown(o); // S
					}
					close(me()); // We're done, close to not come back here again
					return; // Once closed, run no more code here
				}

			} catch (ProgramException e) { exception = e; close(me()); } // Save and close
		}
	}
	
	private Share me() { return this; }
}
