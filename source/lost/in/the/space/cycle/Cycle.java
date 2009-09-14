package lost.in.the.space.cycle;

import java.util.List;

import lost.in.the.space.cycle.pick.Pick;
import lost.in.the.space.cycle.pick.ResultFile;
import lost.in.the.space.program.Bridge;

import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.net.name.Ip;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Once;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.user.Describe;

public class Cycle extends Close {
	

	// me algorithm:
	
	// wait for prerequisites
	// search                                "Searching..."
	// wait for no results in a minute       "22 results"
	// if no results at all, stop            "No results. Please choose a shorter or more common keyword."
	// pick something new to download
	// pick a free path to download it to
	// download                              "Downloaded 0 of 123456789 bytes"
	// watch for done
	// watch for error
	// watch for no progress in a minute
	// repeat
	
	// have this object do a single cycle
	
	public Cycle(Bridge bridge, String keyword, String ext, Path folder) {
		this.bridge = bridge;
		this.keyword = keyword;
		this.ext = ext;
		this.folder = folder;
		
		pulse = new Pulse(new MyReceive());
	}
	
	private final Bridge bridge;
	private final String keyword;
	private final String ext;
	private final Path folder;
	
	private final Pulse pulse;
	
	private Search search;
	
	

	@Override public void close() { //TODO make this also useful for canceling
		if (already()) return;
		close(pulse);
		close(search);
	}

	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return;
				
			if (no(search))
				search = new Search(bridge, keyword, ext);
			if (done(search) && once.once()) {
				Pick.pick(search.files(), ext);

				for (ResultFile file : search.files())
					System.out.println(file.toString());
			}
				
				
				
			//TODO at the end, have the core shut down all the downloads and searches
				
		}
	}
	
	public Once once = new Once();
	
	public String status() {
		if (is(search))
			return search.status();
		return "";
	}
	
	
	/** A search result has come up from the core below. */
	public void result(JSONObject r) {
		if (is(search))
			search.result(r);
	}
	
	
	
	
}
