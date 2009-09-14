package lost.in.the.space.cycle.have;

import org.zootella.cheat.file.Path;

public class HaveFile { // - WillTravel

	
	
	/** The path where we saved this file, null before we've downloaded it. */
	public Path path() { return path; }
	public Path path;
	
	/** Record the Path where we saved this file. */
	public void path(Path path) {
		if (path != null) throw new IllegalStateException(); // Only set this once
		this.path = path;
	}
	
}
