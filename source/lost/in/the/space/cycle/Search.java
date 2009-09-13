package lost.in.the.space.cycle;

import java.util.ArrayList;
import java.util.List;

import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.net.name.Ip;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Once;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.Now;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;
import org.zootella.cheat.user.Describe;

public class Search extends Close {
	
	public Search(Bridge bridge, String keyword, String ext) {
		this.bridge = bridge;
		this.keyword = keyword;
		this.ext = ext;
		
		files = new ArrayList<File>();
		pulse = new Pulse(new MyReceive());
	}
	
	private final Bridge bridge;
	
	private final String keyword;
	private final String ext;
	
	private final Pulse pulse;
	private final List<File> files;
	
	private Now search;

	@Override public void close() {
		if (already()) return;
		close(pulse);
	}

	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return;
			
			if (search == null) { // Just do this once
				search = new Now(); // Record when we started the search
				search(); // Send down the search
			}
			
			if (search != null && search.expired(2 * Time.minute)) {
				close(me());
				return;
			}
		}
	}
	
	private Search me() { return this; }

	/** How many results this Search has received. */
	public int count() {
		return files.size();
	}

	/** The Result we've picked to download, null before we're ready to pick. */
	public List<File> files() {
		if (!closed()) throw new IllegalStateException(); // Don't call this while results are still coming in
		return files;
	}

	private void search() throws JSONException {

		JSONObject p = new JSONObject();
		p.put("keyword", keyword);
		
		JSONObject o = new JSONObject();
		o.put("search", p);
		
		bridge.sendDown(o);
	}

	/** A search result has come up from the core below. */
	public void result(JSONObject o) {
		if (closed()) return; // A closed object promises not to change
		
		File f = File.parse(o); // Parse the text message from the core into a File object
		if (f == null)
			return; // Incorrect text, ignore it
		
		for (File file : files) { // Loop through the File objects we already have
			if (file.hash.equals(f.hash)) { // This new one has the same hash as one in our list
				file.add(f); // Copy in the new names and peers
				return; // Done
			}
		}
		
		files.add(f); // The new file's hash is unique, add it to our list
	}
	
	public String status() {
		if (count() == 0)
			return "Searching...";
		else
			return "Found " + Describe.number(count(), "file");		
	}
}
