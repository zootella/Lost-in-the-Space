package lost.in.the.space.bridge;


import org.json.JSONObject;
import org.limewire.ui.swing.util.SwingUtils;
import org.zootella.cheat.state.Receive;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;

@Singleton public class BridgeServiceImpl implements BridgeService {
	
	private boolean running;

	@Inject public BridgeServiceImpl(LifecycleManager lifecycleManager) {
		this.lifecycleManager = lifecycleManager;
	}
	private static LifecycleManager lifecycleManager;
	    
	@Override public String getServiceName() {
		return org.limewire.i18n.I18nMarker.marktr("Bridge Service");
	}
	    
	@Override public void start() {
		if (!running) {

			SwingUtils.invokeLater(new Runnable() {
	            @Override public void run() {
					System.out.println("Started bridge.");
	            }
			});
			running = true;
		}
	}
	    
	public void stop() {
		running = false;
	}
	    
	public void restart() {}
	    
	public Boolean isServerRunning() {
		return running;
	}
	
	/** Send the other side a message. */
	public void send(String s) {
//        bridge.send(s);
	}
	
	private class MyReceive implements Receive {
		public void receive() {
			
			// A message from the other side has arrived!
			while (true) {
				String s = null; //bridge.receive();
				if (s == null) break;
				
				System.out.println("Received: " + s);
				String response = invoke(s);
//				bridge.send(response);
				System.out.println("Sent: " + response);
			}
		}
	}
	
	private String invoke(String requestString) {
		try {
			
			JSONObject request = new JSONObject(requestString);
			JSONObject response = new JSONObject(); //router.route(request);
			String responseString = response.toString();
			return responseString;

		} catch (Exception e) { return e.toString(); }
	}
	
	
	
	
	
	public static void exit() {
		lifecycleManager.shutdown();
	}
	
	
	
	
	
}
