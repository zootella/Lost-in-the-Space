package lost.in.the.space.program;

import lost.in.the.space.cycle.Cycle;

import org.json.JSONObject;
import org.limewire.util.SystemUtils;
import org.limewire.util.SystemUtils.SpecialLocations;
import org.zootella.cheat.data.Text;
import org.zootella.cheat.exception.DataException;
import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Model;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.After;
import org.zootella.cheat.time.Ago;
import org.zootella.cheat.time.Pulse;
import org.zootella.cheat.time.Time;

public class Core extends Close {
	
	public Core(Program program) {
		this.program = program;

		Receive receive = new MyReceive();
		pulse = new Pulse(receive);

		model = new MyModel();
		model.pulse();
		
		enter = new After(enterTime);
		
		share((new Path(SystemUtils.getSpecialPath(SpecialLocations.DOCUMENTS))).add("Share").toString());
		
		
	}
	public final Program program;
	private final Pulse pulse;
	private final After enter;
	private Cycle cycle;
	

	@Override public void close() {
		if (already()) return;
		close(share);
		close(model);
		close(pulse);
		close(cycle);
	}
	
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return;
			try {
				
				// Every 5 minutes, share everything in the folder
				if (done(share))
					share = null;
				if (shareAgo == null)
					shareAgo = new Ago(shareTime);
				if (no(share) && shareAgo.enough())
					share = new Share(program, folder);
				
				// Run one Cycle after another that does a search and downloads stuff
				if (done(cycle))
					cycle = null;
				if (no(cycle) && loadedReady() && shareReady() && enterReady()) {
					cycle = new Cycle(program.bridge, keyword, ext, folder);
					model.changed();
				}

			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	private boolean loaded;
	public boolean loadedReady() { return loaded; }
	public void loaded() {
		loaded = true;
		model.changed();
	}

	private Path folder;
	public boolean shareReady() { return folder != null; }
	public Path share() { return folder; }
	public String share(String s) {

		Path p = null;
		try { p = new Path(s); } catch (DataException e) { return "That text isn't a valid path."; }
		try { p.folder();      } catch (DiskException e) { return "Cannot find or make folder."; }
		try { p.folderWrite(); } catch (DiskException e) { return "Cannot write in folder."; }

		this.folder = p;
		model.changed();
		
		close(share);
		share = null;
		shareAgo = null;

		return null;
	}
	
	private static final long shareTime = 5 * Time.minute;
	private Ago shareAgo;
	private Share share;
	
	private static final long enterTime = 2 * Time.second;
	private String keyword;
	private String ext;
	public boolean enterReady() { return Text.is(keyword) && Text.is(ext) && enter.enough(); }
	public void enter(String keyword, String ext) {
		this.keyword = keyword;
		this.ext = ext;
		enter.set();
		model.changed();
	}
	
	
	// Model

	public final MyModel model;
	public class MyModel extends Model {
		
		public String share() {
			if (folder == null) return "";
			return folder.toString();
		}
		
		public String status() {
			if (is(cycle)) return cycle.status();

			if (!shareReady()) return "Click above to choose a folder";
			if (!enterReady()) return "Type a search keyword and file extension to begin";
			if (!loadedReady()) return "Loading...";
			
			return "";
		}
	}
	
	
	
	
	public void result(JSONObject r) {
		if (is(cycle))
			cycle.result(r);
		
		
	}
	

}
