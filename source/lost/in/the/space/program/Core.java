package lost.in.the.space.program;

import org.limewire.util.SystemUtils;
import org.limewire.util.SystemUtils.SpecialLocations;
import org.zootella.cheat.exception.DataException;
import org.zootella.cheat.exception.DiskException;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Model;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;
import org.zootella.cheat.time.Pulse;

public class Core extends Close {
	
	public Core(Program program) {
		this.program = program;

		Receive receive = new MyReceive();
		update = new Update(receive);
		pulse = new Pulse(receive);
		update.send();

		model = new MyModel();
		model.changedNow();
		
		share((new Path(SystemUtils.getSpecialPath(SpecialLocations.DOCUMENTS))).add("Shared").toString());
	}
	public final Program program;
	private final Update update;
	private final Pulse pulse;

	@Override public void close() {
		if (already()) return;
		close(model);
		close(pulse);
	}
	
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return;
			
			
			
		}
	}
	
	
	private Path share;
	public Path share() { return share; }
	public String share(String s) {

		Path p = null;
		try { p = new Path(s); } catch (DataException e) { return "That text isn't a valid path."; }
		try { p.folder();      } catch (DiskException e) { return "Cannot find or make folder."; }
		try { p.folderWrite(); } catch (DiskException e) { return "Cannot write in folder."; }

		this.share = p;
		update.send();
		model.changedNow();
		return null;
	}
	
	private String keyword;
	private String ext;
	public void enter(String keyword, String ext) {
		this.keyword = keyword;
		this.ext = ext;
		
		System.out.println("enter: " + keyword + ", " + ext);
	}
	
	
	public void loaded() {
		loaded = true;
		model.changedNow();
	}
	private boolean loaded;
	
	
	// Model

	public final MyModel model;
	public class MyModel extends Model {
		
		public String share() {
			if (share == null)
				return "";
			return share.toString();
		}
		
		public String status() {
			if (share == null)
				return "Choose Folder";
			if (loaded)
				return "Loaded";
			return "Loading...";
		}
	}
	private Core me() { return this; } // Give the inner class a link to this outer object
	
	
	
	

}
