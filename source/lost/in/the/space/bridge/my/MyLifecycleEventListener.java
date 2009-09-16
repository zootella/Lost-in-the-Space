package lost.in.the.space.bridge.my;

import lost.in.the.space.program.Bridge;

import org.limewire.core.api.lifecycle.LifeCycleEvent;
import org.limewire.listener.EventListener;
import org.limewire.listener.SwingEDTEvent;
import org.zootella.cheat.state.Update;

/** Register a EventListener of LifeCycleEvents to find out when the LimeWire API finishes loading and shutting down. */
public class MyLifecycleEventListener implements EventListener<LifeCycleEvent> {
	
	/** Make a MyLifecycleEventListener so we find out when the API loads and is shut down. */
	public MyLifecycleEventListener(Bridge bridge, Update update) {
		this.bridge = bridge;
		this.update = update; // Save a link to the given Update object that wants to know when the API is loaded
	}
	private final Bridge bridge;
	private final Update update;
	
	/** When a LifeCycleEvent happens, the LimeWire API will tell this method about it. */
    @SwingEDTEvent public void handleEvent(LifeCycleEvent event) {
    	
    	if (event == LifeCycleEvent.STARTED) { // Happens a half minute or so after the program starts
    		bridge.sendUp(Bridge.say("loaded")); // Tell the program above we're loaded and ready to go
			update.send(); // Now the LimeWire side of the Bridge can do messages waiting for it
			
    	} else if (event == LifeCycleEvent.SHUTDOWN) { // Happens a few seconds after the program tells the API to quit
    		bridge.sendUp(Bridge.say("quitted")); // Tell the program above we're finally shut down
    	}
    }
}
