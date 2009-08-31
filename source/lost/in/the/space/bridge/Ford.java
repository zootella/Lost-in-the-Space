package lost.in.the.space.bridge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Update;

public class Ford {
	
	// Access
	
	/** Access the program's single Ford object that lets the ui and core pass messages. */
	public synchronized static Ford instance() {
		if (ford == null)
			ford = new Ford();
		return ford;
	}
	private static Ford ford;

	private Ford() {
		down = new ArrayList<JSONObject>();
		up = new ArrayList<JSONObject>();
	}
	private final List<JSONObject> down;
	private final List<JSONObject> up;
	private Update updateDown;
	private Update updateUp;

	// Send
	
	/** Send a message down to the core. */
	public synchronized void sendDown(JSONObject o) {
		System.out.println("USER> " + o.toString());
		down.add(o);
		if (updateDown != null)
			updateDown.send();
	}
	/** Send a message up to the ui. */
	public synchronized void sendUp(JSONObject o) {
		System.out.println("    " + o.toString() + " <CORE");
		up.add(o);
		if (updateUp != null)
			updateUp.send();
	}

	// Receive
	
	/** Find out when the ui sends a message down to the core. */
	public synchronized void updateDown(Update update) {
		updateDown = update;
	}
	/** Find out when the core sends a message up to the ui. */
	public synchronized void updateUp(Update update) {
		updateUp = update;
	}

	/** The next message that went down, or null if no more. */
	public synchronized JSONObject receiveDown() {
		if (down.isEmpty()) return null;
		return down.remove(0);
	}
	/** The next message that went up, or null if no more. */
	public synchronized JSONObject receiveUp() {
		if (up.isEmpty()) return null;
		return up.remove(0);
	}

	// Tools
	
	/** Say key in a JSONObject. */
	public static JSONObject say(String key) {
		return say(key, "-");
	}
	/** Say key and value in a JSONObject. */
	public static JSONObject say(String key, String value) {
		JSONObject o = new JSONObject();
		try {
			o.put(key, value);
		} catch (JSONException e) { Mistake.stop(e); }
		return o;
	}
}
