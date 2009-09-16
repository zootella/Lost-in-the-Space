package lost.in.the.space.cycle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

/** A single Cycle object does a Gnutella search, downloads 3 files, and closes. */
public class Cycle extends Close {
	
	// Object

	/** Make a Cycle object to search Gnutella, download 3 files, and close complete. */
	public Cycle(Bridge bridge, Pick pick, String keyword, String ext, Path folder) {
		this.bridge = bridge; // Save the given links to the parts of the program we'll use
		this.pick = pick;
		
		this.keyword = keyword; // Save the user's current keyword and file extension input
		this.ext = ext;
		this.folder = folder; // And also the path to the current saved folder
		
		after = new After(Time.minute); // Notice after a minute has passed after the most recent after.set() call
		downloads = new ArrayList<Download>(); // Make a new empty List that will hold Download objects
		pulse = new Pulse(new MyReceive()); // Call receive() to notice when we've waited long enough
		once = new Once(); // Notice once when our search has finished
	}
	
	/** A link to the program's Bridge object that lets us send commands down to the LimeWire API. */
	private final Bridge bridge;
	/** A link to the program's Pick object that keeps track of hashes we have and paths we've used to pick what to get next. */
	private final Pick pick;
	/** The search keyword the user typed. */
	private final String keyword;
	/** The file extension the user typed. */
	private final String ext;
	/** The absolute disk Path to the current share folder where we'll save our new downloads. */
	private final Path folder;

	/** Calls Receive so we notice when we've waited long enough to try the next thing. */
	private final Pulse pulse;
	
	/** Our Search object that does a Gnutella search. */
	private Search search;
	/** Notice once when our Search has finished. */
	private final Once once;

	/** Our List of 0, 1, 2, or 3 different Download objects. */
	private List<Download> downloads;
	/** An After object that notices when no new bytes have been downloaded for a minute. */
	private final After after;

	/** Close everything we made and promise to not change again. */
	@Override public void close() {
		if (already()) return;
		close(pulse); // Close everything we made that needs closing
		close(search);

		for (Download download : downloads) // Loop for each of our downloads
			bridge.sendDown(Bridge.say("cancel", download.hash)); // Cancel it with the API below

		Set<String> guids = new HashSet<String>();
		for (ResultFile file : search.files)
			guids.add(file.search); // There should be only one search GUID, but make a unique list to be sure
		for (String guid : guids)
			bridge.sendDown(Bridge.say("stop", guid)); // Cancel it, or them
	}
	
	// Receive

	/** Notice when things have finished or we've waited long enough to move on to the next step. */
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return; // A closed object promises to do nothing and never change again
				
			if (no(search)) // We don't have our Search object yet
				search = new Search(bridge, keyword, ext); // So make it already
			if (done(search) && // Minutes later, it's finished, and
				once.once()) {  // And we notice that here for the first time
				after.set();    // Now the requirement is some download progress every minute
				
				List<ResultFile> picked = new ArrayList<ResultFile>();
				picked = pick.pick(search.files, ext);

				System.out.println("\nresults:");
				for (ResultFile file : search.files)
					System.out.println(file.toString());
				System.out.println("\npicked:");
				for (ResultFile file : picked)
					System.out.println(file.toString());

				for (ResultFile file : picked) {
					downloads.add(new Download(file.hash, file.size));
					download(bridge, file.search, file.hash, pick.name(folder, file.names.iterator().next()));
				}
			}
			
			for (Download download : downloads) // We have some downloads
				bridge.sendDown(Bridge.say("progress", download.hash)); // Ask down for their current progress
			
			if (after.enough()) { // It's been over a minute with no new bytes downloaded at all
				close(me()); // This Cycle is finished
				return; // Leave now to be sure no code in receive() runs in a closed object
			}
		}
	}

	/** Give inner code a link to this outer object. */
	private Cycle me() { return this; }
	
	// Status
	
	/** Compose text for the user that describes what's going on right now. */
	public String status() {
		
		if (!downloads.isEmpty()) { // We've finished searching, and are downloading now
			long saved = 0;
			long size = 0;
			for (Download download : downloads) {
				saved += download.saved; // Total up the bytes saved and total size
				size += download.size;
			}
			return "Downloaded " + saved + " of " + size + " bytes"; // Say it
		}
		
		if (is(search)) // We're still searching
			return search.status(); // Have our Search object report its status
		
		return ""; // We're about to start searching
	}
	
	// Help
	
	/** Tell the API below to start a new download. */
	public static void download(Bridge bridge, String guid, String hash, Path path) throws JSONException {
		
		JSONObject p = new JSONObject(); // Fill out the "guid", "hash", and "path" parameters
		p.put("guid", guid);
		p.put("hash", hash);
		p.put("path", path.toString());
		
		JSONObject o = new JSONObject(); // Put all that beneath "download"
		o.put("download", p);
		
		bridge.sendDown(o); // Send the command down the Bridge to the LimeWire API below
	}
	
	// Message

	/** A search result has come up from the LimeWire API below. */
	public void result(JSONObject r) {
		if (is(search))
			search.result(r); // Give it to our Search object
	}
	
	/** Download progress has come up from the LimeWire API below. */
	public void progress(JSONObject r) {
		try {
			r = r.getJSONObject("progress"); // Move beneath the "progress" heading of the message
			for (Download download : downloads) {
				if (download.hash.equals(r.getString("hash"))) { // Find our Download this is the progress of
					if (download.saved < r.getLong("saved")) { // Only do something if this is forward progress
						download.saved = r.getLong("saved");
						after.set(); // Record we saved a byte as recently as right now
					}
				}
			}
		} catch (JSONException e) { Mistake.log(e); } // Shouldn't happen, and not a big deal
	}
}
