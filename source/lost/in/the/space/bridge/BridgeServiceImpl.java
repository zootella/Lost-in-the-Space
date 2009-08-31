package lost.in.the.space.bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.exception.DataException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;

@Singleton public class BridgeServiceImpl implements BridgeService {
	
	// Inject

	@Inject public BridgeServiceImpl(LifecycleManager lifecycleManager) {
		this.lifecycleManager = lifecycleManager;
		loaded = true;
		
		ford = Ford.instance();
		//TODO what thread is this? you probably need to synchronize ford
		//TODO send an update to tell the window above that now we're ready
	}
	private static LifecycleManager lifecycleManager;
	private static volatile boolean loaded;
	
	private final Ford ford;
	
	// Service
	    
	@Override public String getServiceName() {
		return org.limewire.i18n.I18nMarker.marktr("Bridge Service");
	}
	@Override public void start() {
		if (!running) {
			running = true;
			System.out.println("Started bridge.");
		}
	}
	public void restart() {}
	public void stop() {
		running = false;
	}
	public Boolean isServerRunning() {
		return running;
	}
	private boolean running;

	// Command
	
	public static boolean isReady() {
		return loaded;
	}

	public static JSONObject command(JSONObject o) {
		if (!loaded) throw new IllegalStateException();
		try {
			
			if (o.has("quit")) {
				
				System.out.println("before shutdown");
				lifecycleManager.shutdown();
				System.out.println("after shutdown");
				return say("result", "ok");

			} else {
				return say("result", "unknown command");
			}

		} catch (Exception e) {
			return say("result", e.toString());
		}
	}
	
	
	
	public static JSONObject say(String name, String value) {
		JSONObject r = new JSONObject();
		try {
			r.put(name, value);
		} catch (JSONException e) { throw new DataException(e); }
		return r;
	}
	
	
	
	
	
}
