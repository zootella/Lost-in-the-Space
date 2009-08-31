package lost.in.the.space.bridge;

import org.json.JSONObject;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;

@Singleton public class BridgeServiceImpl implements BridgeService {
	
	// Inject

	@Inject public BridgeServiceImpl(LifecycleManager lifecycleManager) {
		// Save references to injected parts of LimeWire
		this.lifecycleManager = lifecycleManager;

		// Connect to the Ford that lets us talk to the window above
		update = new Update(new MyReceive());
		update.send();
		ford = Ford.instance();
		ford.updateDown(update);
	}
	
	private final LifecycleManager lifecycleManager;
	
	private final Update update;
	private final Ford ford;

	// Service
	    
	@Override public String getServiceName() {
		return org.limewire.i18n.I18nMarker.marktr("Bridge Service");
	}
	@Override public void start() {
		if (!running) {
			running = true;
			System.out.println("Started bridge.");
			ford.sendUp(Ford.say("loaded"));
			update.send();
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

	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			if (!running) return;
			
			while (true) {
				JSONObject o = ford.receiveDown();
				if (o == null)
					return;
				JSONObject r = command(o);
				if (r != null)
					ford.sendUp(r);
			}
		}
	}
	
	public JSONObject command(JSONObject o) {
		try {
			
			if (o.has("quit")) {
				
				System.out.println("before shutdown");
				lifecycleManager.shutdown();
				System.out.println("after shutdown");
				return Ford.say("quitted");

			} else {
				return Ford.say("result", "unknown command");
			}

		} catch (Exception e) {
			return Ford.say("result", e.toString());
		}
	}
	
	
	
	
	
	
	
	
}
