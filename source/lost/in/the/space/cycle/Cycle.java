package lost.in.the.space.cycle;

import java.util.HashSet;
import java.util.Set;

import lost.in.the.space.program.Core.MyModel;

import org.json.JSONObject;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Model;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.Pulse;

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
	
	public Cycle(String keyword, String ext, Path folder) {
		this.keyword = keyword;
		this.ext = ext;
		this.folder = folder;
		
		pulse = new Pulse(new MyReceive());
		model = new MyModel();
		
		files = new HashSet<File>();
	}
	
	private final String keyword;
	private final String ext;
	private final Path folder;
	
	private final Pulse pulse;
	
	private final Set<File> files;
	
	

	@Override public void close() { //TODO make this also useful for canceling
		if (already()) return;
		close(pulse);
		close(model);
	}

	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return;
			try {
				
				
				
				
				
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	
	public final MyModel model;
	public class MyModel extends Model {
		
		public String status() {
			return "cycle status";
		}
	}
	private Cycle me() { return this; } // Give inner classes a link to this outer object
	
	
	/** A search result has come up from the core below. */
	public void result(JSONObject r) {
		
		File file = File.parse(r);
		if (file != null)
			files.add(file);
		
		
		
	}
	
	
}
