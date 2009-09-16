package lost.in.the.space.cycle;

import java.util.ArrayList;
import java.util.List;

import lost.in.the.space.cycle.pick.Pick;
import lost.in.the.space.cycle.pick.ResultFile;
import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.data.Text;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.Now;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;
import org.zootella.cheat.user.Describe;

/** A Search object performs a Gnutella search, and collects the results. */
public class Search extends Close {

	/** Make a Search object to search Gnutella for something, and collect the results. */
	public Search(Bridge bridge, String keyword, String ext) {
		this.bridge = bridge; // Save the given link to the program's Bridge
		
		this.keyword = keyword; // Save the user's chosen keyword and file extension
		this.ext = ext;
		
		files = new ArrayList<ResultFile>(); // Make a new empty list that will hold ResultFile objects
		pulse = new Pulse(new MyReceive()); // Notice when the search has gone on for 2 minutes
	}

	/** A link to the program's Bridge that lets us talk down to the LimeWire API below. */
	private final Bridge bridge;

	/** The user's search keyword. */
	private final String keyword;
	/** The file extension the user typed. */
	private final String ext;

	/** Calls receive() so we notice when we've been searching for 2 minutes. */
	private final Pulse pulse;
	/** Our List of search results. */
	public final List<ResultFile> files;
	/** The time when we started the search. */
	private Now search;

	/** Close all our resources and promise to not change again. */
	@Override public void close() {
		if (already()) return;
		close(pulse); // We stop the search in Cycle, not here, because the downloads might need it
	}

	/** Start a new search, and notice 2 minutes later. */
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return;
			
			if (search == null) { // Just do this once
				search = new Now(); // Record when we started the search
				search(); // Send down the search
			}
			
			if (search != null && search.expired(2 * Time.minute)) { // 2 minutes later
				close(me()); // Close this object
				return; // Now that we're closed, we can't run any more code in receive()
			}
		}
	}

	/** Give inner code a link to this outer object. */
	private Search me() { return this; }

	/** Send a new search down to the LimeWire API below. */
	private void search() throws JSONException {
		JSONObject p = new JSONObject(); // Specify the user's keyword
		p.put("keyword", keyword);

		String type = Pick.extension(ext);
		if (Text.is(type)) // If the user gave us a common file extension
			p.put("type", type); // Narrow the Gnutella search to the associated media type

		JSONObject o = new JSONObject(); // Put all that under "search" and send it down
		o.put("search", p);
		bridge.sendDown(o);
	}

	/** A search result has come up from the core below. */
	public void result(JSONObject o) {
		if (closed()) return; // A closed object promises not to change
		
		ResultFile f = ResultFile.parse(o); // Parse the text message from the core into a File object
		if (f == null)
			return; // Incorrect text, ignore it
		
		for (ResultFile file : files) { // Loop through the ResultFile objects we already have
			if (file.hash.equals(f.hash)) { // This arrival has the same hash as one in our list
				file.add(f); // Merge in the additional names and sharing peers
				return; // Done, don't do the last line of code in this method
			}
		}
		files.add(f); // The arrival file's hash is unique, add it to our list of results
	}

	/** What are we doing? Right now. */
	public String status() {
		if (files.isEmpty())
			return "Searching..."; // No results yet, so be vague
		else
			return "Found " + Describe.number(files.size(), "file"); // Brag how many results we've bagged
	}
}
