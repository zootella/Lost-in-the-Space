package lost.in.the.space.program;

import lost.in.the.space.cycle.Cycle;
import lost.in.the.space.cycle.Share;
import lost.in.the.space.cycle.pick.Pick;

import org.json.JSONObject;
import org.limewire.util.SystemUtils;
import org.limewire.util.SystemUtils.SpecialLocations;
import org.zootella.cheat.data.Text;
import org.zootella.cheat.exception.DataException;
import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Model;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.After;
import org.zootella.cheat.time.Ago;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;

/** The program's Core object is everything except the user interface on the screen. */
public class Core extends Close {
	
	// Object

	/** Make the Core object to do everything except communicate with the user. */
	public Core(Program program) {
		this.program = program; // Save the given link to the Program object we're a part of

		Receive receive = new MyReceive(); // Our Receive method gets called when stuff we care about has changed
		pulse = new Pulse(receive);
		model = new MyModel(); // Our Model object gives up-to-date information to our Window's View above
		model.pulse();
		
		enter = new After(enterTime); // Notice when it's been 2 seconds since the user stopped typing in the Window above
		pick = new Pick(); // Make our Pick object which picks what to download next
		
		share((new Path(SystemUtils.getSpecialPath(SpecialLocations.DOCUMENTS))).add("Share").toString()); // Default folder
	}
	
	/** A link back up to the Program object we're a part of. */
	public final Program program;
	/** A Pulse that calls receive() so we can tell when time intervals like 2 seconds and 5 minutes are up. */
	private final Pulse pulse;
	/** Picks new stuff to get by keeping track of hashes already downloaded and file names already in use. */
	public final Pick pick; // final because there's only one of these that lasts the whole time the program runs
	
	/** Does a Gnutella search, downloads 3 files, and closes. */
	private Cycle cycle; // Reference not final because we only have one Cycle at a time, but make one after another

	/** Close resources and promise to not change again. */
	@Override public void close() {
		if (already()) return; // Only go in here once
		close(share); // Close every internal object that extends Close, like we do
		close(model);
		close(pulse);
		close(cycle);
	}
	
	// Receive

	/** Something we care about may have changed. */
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return; // Don't do anything once closed
				
			if (done(share)) // Every 5 minutes, share everything in the folder
				share = null;
			if (shareAgo == null)
				shareAgo = new Ago(shareTime);
			if (no(share) && shareAgo.enough())
				share = new Share(program.bridge, folder);
			
			if (done(cycle)) // Run one Cycle after another that does a search and downloads stuff
				cycle = null;
			if (no(cycle) && loadedReady() && shareReady() && enterReady()) {
				cycle = new Cycle(program.bridge, pick, keyword, ext, folder);
				model.changed();
			}
		}
	}
	
	// Ready
	
	/** true once the LimeWire API beneath has loaded and is ready for us to talk to it. */
	public boolean loadedReady() { return loaded; }
	private boolean loaded; // Starts out false
	/** Call this once the LimeWire API beneath has loaded, so we can make a note of that. */
	public void loaded() { loaded = true; } // This method sets it to true

	/** The disk Path to the folder where the program saves public files. */
	public Path share() { return folder; }
	private Path folder; // Starts out null
	/** true if we've got a folder ready to use. */
	public boolean shareReady() { return folder != null; }
	/** Set our share folder to the disk path s and return null, or text for the user why s isn't a good path. */
	public String share(String s) {

		Path p = null; // Try parsing s into a Path, then checking it's a folder on the disk that we can write in
		try { p = new Path(s); } catch (DataException e) { return "That text isn't a valid path."; }
		try { p.folder();      } catch (DiskException e) { return "Cannot find or make folder."; }
		try { p.folderWrite(); } catch (DiskException e) { return "Cannot write in folder."; }

		this.folder = p; // Save the new Path
		
		close(share); // If we've got a task sharing the contents of our folder right now, close it
		share = null; // And discard it
		shareAgo = null; // And forget about when we last did it

		return null; // Report success
	}
	
	/** 5 minutes in milliseconds, every 5 minutes share the contents of the folder. */
	private static final long shareTime = 5 * Time.minute;
	/** When we last shared the contents of the folder. */
	private Ago shareAgo;
	/** A Share object currently sharing the contents of the folder, or null if none in use right now. */
	private Share share;

	/** 2 seconds in milliseconds, we wait 2 seconds after the user's last keystroke before looking at what she typed. */
	private static final long enterTime = 2 * Time.second;
	/** Keeps track of when the user stopped typing, and tells if it's been long enough afterwards. */
	private final After enter;
	/** The search keyword the user typed, null beforehand and maybe "". */
	private String keyword;
	/** The search file extension the user typed, null beforehand and maybe "". */
	private String ext;
	/** true when the user typed keyword and extension text and moved away from the keyboard. */
	public boolean enterReady() { return Text.is(keyword) && Text.is(ext) && enter.enough(); }
	/** The user has entered different keyword and extension text. */
	public void enter(String keyword, String ext) {
		this.keyword = keyword; // Save what's come in from above
		this.ext = ext;
		enter.set(); // Note the time this happened right now
	}

	// Model

	/** Our Model communicates our current state with views above, like the View in Window. */
	public final MyModel model;
	public class MyModel extends Model {

		/** Path to the share folder. */
		public String share() {
			if (folder == null) return ""; // We don't have a folder right now
			return folder.toString();
		}

		/** A line of text for the user that says what the program is doing right now. */
		public String status() {
			if (is(cycle)) return cycle.status(); // When we have a Cycle object, have it say for us

			if (!shareReady()) return "Click above to choose a folder"; // Tell the user what she needs to do
			if (!enterReady()) return "Type a search keyword and file extension to begin";
			if (!loadedReady()) return "Loading..."; // User input good, just waiting for the LimeWire API to finish loading

			return ""; // The next Cycle is about to begin
		}
	}
	
	// Message

	/** The LimeWire API below sent up a "result" message. */
	public void result(JSONObject r) {
		if (is(cycle)) // If we have a Cycle object right now
			cycle.result(r); // Give it the message
	}
	
	/** The LimeWire API below sent up a "progress" message. */
	public void progress(JSONObject r) {
		if (is(cycle))
			cycle.progress(r); // Give it to our current Cycle object
	}
}
