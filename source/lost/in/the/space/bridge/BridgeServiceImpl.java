package lost.in.the.space.bridge;

import org.json.JSONObject;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;

import org.limewire.core.api.lifecycle.LifeCycleEvent;
import org.limewire.listener.EventListener;
import org.limewire.listener.SwingEDTEvent;

@Singleton public class BridgeServiceImpl implements BridgeService {
	
	// Inject

	@Inject public BridgeServiceImpl(LifecycleManager lifecycleManager) {
		
		// Save references to injected parts of LimeWire
		this.lifecycleManager = lifecycleManager;

		lifecycleManager.addListener(new MyLifecycleEventListener());

		// Connect to the Ford that lets us talk to the window above
		update = new Update(new MyReceive());
		update.send(); // There may already be a message waiting for us
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
	
	// Event

	private class MyLifecycleEventListener implements EventListener<LifeCycleEvent> {
        @SwingEDTEvent public void handleEvent(LifeCycleEvent event) {
        	
        	if (event == LifeCycleEvent.STARTED) {
        		ford.sendUp(Ford.say("loaded"));
    			update.send(); // Now we will be able to do a message waiting for us
    			
        	} else if (event == LifeCycleEvent.SHUTDOWN) {
        		ford.sendUp(Ford.say("quitted"));
        	}
        }
	}

	// Command

	// Java calls this when a new message was sent down for us
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			// Don't do messages until this service is running
			if (!running) return;

			// Loop for each message that has arrived from the ui above
			while (true) {
				JSONObject o = ford.receiveDown();
				if (o == null)
					return; // No more messages right now
				
				// Do the message
				JSONObject r = message(o);
				if (r != null)
					ford.sendUp(r); // Send a response
			}
		}
	}
	
	/** Do what o from the ui above tells us to do, and send a reply back up or null for none. */
	public JSONObject message(JSONObject o) {
		try {
			
			if (o.has("quit")) {
				
				lifecycleManager.shutdown();
				return null;

			} else {
				return Ford.say("unknown");
			}

		} catch (Exception e) {
			return Ford.say("exception", e.toString());
		}
	}
}
