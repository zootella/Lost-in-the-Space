package lost.in.the.space.program;

import lost.in.the.space.bridge.BridgeServiceImpl;
import lost.in.the.space.bridge.Ford;
import lost.in.the.space.user.Window;

import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;

public class Program extends Close {
	
	public Program() {
		this.window = new Window(this);
		this.ford = Ford.instance();
		
	}
	public final Window window;
	public final Ford ford;

	@Override public void close() {
		if (already()) return;
		
		close(window);
		Mistake.closeCheck();
		
		if (BridgeServiceImpl.isReady()) {
			BridgeServiceImpl.command(BridgeServiceImpl.say("quit", ""));
		}
		
		System.out.println("system exit");
		System.exit(0);
	}
}