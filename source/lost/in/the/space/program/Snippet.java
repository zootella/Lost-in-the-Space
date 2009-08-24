package lost.in.the.space.program;

import lost.in.the.space.bridge.BridgeServiceImpl;

import org.json.JSONObject;

public class Snippet {
	
	public static void snippet() throws Exception {
		

		// shut down the core
		
		if (BridgeServiceImpl.isReady()) {
			JSONObject r = BridgeServiceImpl.command(BridgeServiceImpl.say("quit", ""));
			System.out.println(r.toString());
		} else {
			System.out.println("not ready");
		}
		
		
		
		
		
		
	}

}
