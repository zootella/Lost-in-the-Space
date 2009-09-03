package lost.in.the.space.program;

import java.util.List;

import org.json.JSONObject;
import org.zootella.cheat.exception.ProgramException;
import org.zootella.cheat.file.ListTask;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;
import org.zootella.cheat.time.Egg;
import org.zootella.cheat.time.Time;

public class Share extends Close {
	
	public Share(Program program, Path folder) {
		this.program = program;
		this.folder = folder;
		
		Receive receive = new MyReceive();
		update = new Update(receive);
		update.send();
		
		egg = new Egg(receive, 20 * Time.second);
	}
	
	private final Program program;
	
	private final Path folder;
	private final Update update;
	private final Egg egg;
	
	private ListTask task;
	private List<Name> list;
	
	public ProgramException exception() { return exception; }
	private ProgramException exception;

	@Override public void close() {
		if (already()) return;
		close(task);
		close(egg);
	}
	
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return;
			try {

				egg.check(); // Make sure this whole thing doesn't take too long
				
				if (no(task))
					task = new ListTask(update, folder);
				if (done(task))
					list = task.result();
				
				if (list != null) {
					for (Name name : list) {
						JSONObject o = new JSONObject();
						o.put("share", folder.add(name).toString());
						program.bridge.sendDown(o);
					}
					close(me());
					return;
				}

			} catch (ProgramException e) { exception = e; close(me()); }
		}
	}
	
	private Share me() { return this; }
}
