package lost.in.the.space.cycle;

import lost.in.the.space.program.Program;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.process.Mistake;
import org.zootella.cheat.state.Close;
import org.zootella.cheat.state.Once;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.time.Pulse;

public class Search extends Close {
	
	public Search(Program program, String keyword, String ext) {
		this.program = program;
		this.keyword = keyword;
		this.ext = ext;
		pulse = new Pulse(new MyReceive());
		
		search = new Once();
		
		
		
	}
	
	private final Program program;
	
	private final String keyword;
	private final String ext;
	private final Pulse pulse;
	
	private final Once search;
	


	@Override public void close() {
		if (already()) return;
		
	}
	
	
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			if (closed()) return;
			try {
				
				if (search.once())
					search();
				
				
				
				
				
			} catch (Exception e) { Mistake.stop(e); }
		}
	}
	
	
	
	/** How many results this Search has received. */
	public int count() {
		
		return 0;
		
	}

	/** True if this Search got no results. */
	public boolean none() {

		return false;
	}

	/** The Result we've picked to download, null before we're ready to pick. */
	public Hash pick() {
		
		return null;
		
	}
	
	
	
	
	private void search() throws JSONException {

		JSONObject p = new JSONObject();
		p.put("keyword", keyword);
		
		JSONObject o = new JSONObject();
		o.put("search", p);
		
		program.bridge.sendDown(o);
	}
	
	

}
