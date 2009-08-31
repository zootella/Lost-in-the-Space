package lost.in.the.space.bridge;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Receive;

public class Ford {
	
	//get access to it
	public synchronized static Ford instance() {
		if (ford == null)
			ford = new Ford();
		return ford;
	}
	private static Ford ford;

	//make
	private Ford() {
		down = new ArrayList<JSONObject>();
		up = new ArrayList<JSONObject>();
		receive = new ArrayList<Receive>();
	}
	private final List<JSONObject> down;
	private final List<JSONObject> up;
	private final List<Receive> receive;

	//send a message down or up
	public synchronized void sendDown(JSONObject o) {
		down.add(o);
		arrived();
	}
	public synchronized void sendUp(JSONObject o) {
		up.add(o);
		arrived();
	}

	//get a message that went down or up
	public synchronized JSONObject receiveDown() {
		if (down.isEmpty()) return null;
		return down.remove(0);
	}
	public synchronized JSONObject receiveUp() {
		if (up.isEmpty()) return null;
		return up.remove(0);
	}
	
	//sign up so you get called when new messages arrive
	public synchronized void subscribe(Receive r) {
		receive.add(r);
	}
	private void arrived() {
		try {
			for (Receive r : receive) {
				r.receive();
			}
		} catch (Exception e) { Mistake.stop(e); }
	}
	
	
	
	
	
	
	

}
