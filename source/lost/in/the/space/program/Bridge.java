package lost.in.the.space.program;

import java.util.ArrayList;
import java.util.List;

import lost.in.the.space.bridge.keep.Searches;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.state.Update;

/** The program and the LimeWire API share access to a single Bridge object that shuttles messages up and down between them. */
public class Bridge {
	
	// Access
	
	/** Access the program's single Bridge object that lets the program above and LimeWire API below pass messages. */
	public synchronized static Bridge instance() { // public methods syncrhonized because who knows what rogue LimeWire threads will call us
		if (bridge == null)
			bridge = new Bridge(); // The first time this gets called, make the single Bridge object
		return bridge; // Return our reference to the single Bridge object we just made, or saved a long time ago
	}
	/** The program's single Bridge object. */
	private static Bridge bridge;

	/** Make the program's single Bridge object. */
	private Bridge() { // private to make callers go through the instance() method above
		down = new ArrayList<JSONObject>(); // New empty lists that will hold messages going up and down
		up = new ArrayList<JSONObject>();
		
		searches = new Searches();
	}
	
	/** The Bridge's list of messages going down from the program to the LimeWire API. */
	private final List<JSONObject> down;
	/** The Bridge's list of messages going up from the LimeWire API to the program. */
	private final List<JSONObject> up;
	/** The Update to send when a new message is going down, null before an Update object is set. */
	private Update updateDown;
	/** The Update to send when a new message is going up, null before an Update object is set. */
	private Update updateUp;

	/** On the LimeWire API side of the bridge, we have to keep Search and SearchResult objects the API gives us. */
	public final Searches searches;

	// Send
	
	/** The program above calls sendDown(o) to send message o down to the LimeWire API. */
	public synchronized void sendDown(JSONObject o) {
		System.out.println("USER> " + o.toString()); // Show the communication stream to the programmer
		down.add(o); // Add the new message to the list
		if (updateDown != null)
			updateDown.send(); // Have Java in a moment call updateDown's receive() method
	}
	/** The LimeWire API below calls sendUp(o) to send message o up to the program above. */
	public synchronized void sendUp(JSONObject o) {
		System.out.println("    " + o.toString() + " <CORE"); // Put CORE on the right to make it look like an iChat conversation
		up.add(o);
		if (updateUp != null)
			updateUp.send();
	}

	// Receive
	
	/** Find out when the ui sends a message down to the core. */
	public synchronized void updateDown(Update update) {
		updateDown = update; // Save the given object
	}
	/** Find out when the core sends a message up to the ui. */
	public synchronized void updateUp(Update update) {
		updateUp = update;
	}

	/** The next message that went down, or null if no more. */
	public synchronized JSONObject receiveDown() {
		if (down.isEmpty()) return null; // Fresh out
		return down.remove(0); // Remove and return the first message in the list
	}
	/** The next message that went up, or null if no more. */
	public synchronized JSONObject receiveUp() {
		if (up.isEmpty()) return null;
		return up.remove(0);
	}

	// Tools
	
	/** Say key in a JSONObject. */
	public static JSONObject say(String key) {
		return say(key, "-"); // Hyphen in place of value
	}
	/** Say key and value in a JSONObject. */
	public static JSONObject say(String key, String value) {
		JSONObject o = new JSONObject();
		try {
			o.put(key, value);
		} catch (JSONException e) { throw new IllegalArgumentException(e); }
		return o;
	}
}
