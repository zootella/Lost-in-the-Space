package lost.in.the.space.program;

import lost.in.the.space.user.Window;

import org.json.JSONObject;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

public class Program extends Close {
	
	public Program() {
		this.core = new Core(this);
		this.window = new Window(this);

		// Connect to the Ford that lets us talk to the window above
		update = new Update(new MyReceive());
		update.send();
		bridge = Bridge.instance();
		bridge.updateUp(update); // We want to find out when messages come up
	}
	
	public final Window window;
	public final Core core;
	public final Bridge bridge;
	
	private final Update update;

	@Override public void close() {
		if (already()) return;
		close(window);
		close(core);
		Mistake.closeCheck();
		bridge.sendDown(Bridge.say("quit"));
	}

	// Command

	// Java calls this when a new message was sent up for us
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			// Loop for each message that has arrived from the core below
			while (true) {
				JSONObject r = bridge.receiveUp();
				if (r == null)
					return; // No more messages right now
				
				// Do the message
				message(r);
			}
		}
	}
	
	/** Look at r from the core below. */
	public void message(JSONObject r) {
		
		if (r.has("loaded")) {
			core.loaded();
		} else if (r.has("quitted")) {
			System.exit(0);
		}
	}
}
