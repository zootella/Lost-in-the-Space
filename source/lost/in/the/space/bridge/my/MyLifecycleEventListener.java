package lost.in.the.space.bridge.my;

import lost.in.the.space.program.Bridge;

import org.limewire.core.api.lifecycle.LifeCycleEvent;
import org.limewire.listener.EventListener;
import org.limewire.listener.SwingEDTEvent;
import org.zootella.cheat.state.Update;

public class MyLifecycleEventListener implements EventListener<LifeCycleEvent> {
	
	public MyLifecycleEventListener(Bridge ford, Update update) {
		this.ford = ford;
		this.update = update;
	}
	private final Bridge ford;
	private final Update update;
	
    @SwingEDTEvent public void handleEvent(LifeCycleEvent event) {
    	
    	if (event == LifeCycleEvent.STARTED) {
    		ford.sendUp(Bridge.say("loaded"));
			update.send(); // Now we will be able to do a message waiting for us
			
    	} else if (event == LifeCycleEvent.SHUTDOWN) {
    		ford.sendUp(Bridge.say("quitted"));
    	}
    }
}
