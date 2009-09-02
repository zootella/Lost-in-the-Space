package lost.in.the.space.program;

import java.util.List;

import org.zootella.cheat.exception.ProgramException;
import org.zootella.cheat.file.ListTask;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

public class Share extends Close {
	
	public Share(Program program, Path folder) {
		this.program = program;
		this.folder = folder;
		
		System.out.println("Share " + folder.toString());

		update = new Update(new MyReceive());
		update.send();
	}
	private final Program program;
	
	private final Path folder;
	private final Update update;
	
	private ListTask task;
	private List<Name> list;
	
	public ProgramException exception() { return exception; }
	private ProgramException exception;

	@Override public void close() {
		if (already()) return;
		close(task);
	}
	
	private class MyReceive implements Receive {
		public void receive() throws Exception {
			if (closed()) return;
			try {
				
				if (no(task))
					task = new ListTask(update, folder);
				if (done(task))
					list = task.result();
				
				if (list != null) {
					
					for (Name name : list) {
						Path p = folder.add(name);
						System.out.println("want to share " + p.toString());
						
						
					}
					
				}
				
				
			} catch (ProgramException e) { exception = e; close(me()); }
			
			
			
			
			
			
		}
	}
	
	private Share me() { return this; }
	
	
	
	

}
