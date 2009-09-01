package lost.in.the.space.program;

import lost.in.the.space.bridge.Ford;
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
		ford = Ford.instance();
		ford.updateUp(update);
	}
	
	public final Window window;
	public final Core core;
	public final Ford ford;
	
	private final Update update;

	@Override public void close() {
		if (already()) return;
		close(window);
		close(core);
		Mistake.closeCheck();
		ford.sendDown(Ford.say("quit"));
	}

	// Command

	// Java calls this when a new message was sent up for us
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			// Loop for each message that has arrived from the core below
			while (true) {
				JSONObject r = ford.receiveUp();
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
