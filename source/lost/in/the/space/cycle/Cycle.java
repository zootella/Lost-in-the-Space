package lost.in.the.space.cycle;

import java.util.ArrayList;
import java.util.List;

import lost.in.the.space.cycle.pick.Pick;
import lost.in.the.space.cycle.pick.ResultFile;
import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Once;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.After;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;

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
	
	public Cycle(Bridge bridge, Pick pick, String keyword, String ext, Path folder) {
		this.bridge = bridge;
		this.pick = pick;
		this.keyword = keyword;
		this.ext = ext;
		this.folder = folder;
		
		after = new After(Time.minute);
		
		downloads = new ArrayList<Download>();
		
		pulse = new Pulse(new MyReceive());
	}
	private final Bridge bridge;
	private final Pick pick;
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
				after.set();
				
				List<ResultFile> picked = new ArrayList<ResultFile>();
				picked = pick.pick(search.files(), ext);

				System.out.println("\nresults:");
				for (ResultFile file : search.files())
					System.out.println(file.toString());
				System.out.println("\npicked:");
				for (ResultFile file : picked)
					System.out.println(file.toString());

				for (ResultFile file : picked) {
					downloads.add(new Download(file.hash, file.size));
					download(bridge, file.search, file.hash, pick.name(folder, file.names.iterator().next()));
				}
			}
			
			for (Download download : downloads)
				bridge.sendDown(Bridge.say("progress", download.hash));
			
			if (after.enough()) {
				for (Download download : downloads) {
					bridge.sendDown(Bridge.say("cancel", download.hash));
				}
				close(me());
				return;
			}
				
				
				
				
				
				
				
			//TODO at the end, have the core shut down all the downloads and searches
				
		}
	}
	
	private Cycle me() { return this; }
	
	public Once once = new Once();
	private List<Download> downloads;
	private final After after;
	
	public String status() {
		if (!downloads.isEmpty()) {
			long saved = 0;
			long size = 0;
			for (Download download : downloads) {
				saved += download.saved;
				size += download.size;
			}
			return "Downloaded " + saved + " of " + size + " bytes";
		}
		if (is(search))
			return search.status();
		return "";
	}
	
	public static void download(Bridge bridge, String guid, String hash, Path path) throws JSONException {
		
		JSONObject p = new JSONObject();
		p.put("guid", guid);
		p.put("hash", hash);
		p.put("path", path.toString());
		
		JSONObject o = new JSONObject();
		o.put("download", p);
		
		bridge.sendDown(o);
	}
	
	
	
	/** A search result has come up from the core below. */
	public void result(JSONObject r) {
		if (is(search))
			search.result(r);
	}
	
	public void progress(JSONObject r) {
		try {
			r = r.getJSONObject("progress");
			for (Download download : downloads) {
				if (download.hash.equals(r.getString("hash"))) {
					if (download.saved < r.getLong("saved")) {
						download.saved = r.getLong("saved");
						after.set();
					}
				}
			}
		} catch (JSONException e) { Mistake.log(e); }
	}
	
	
	
	
}
