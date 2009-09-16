package lost.in.the.space.cycle;

/** A Download object holds identifying information about a download we asked the LimeWire API to do. */
public class Download {
	
	// Object

	/** Bundle information that identifies a downlaod together into a Download object. */
	public Download(String hash, long size) {
		this.hash = hash; // Save the file hash and size
		this.size = size;
	}
	
	/** The SHA1 hash of the file. */
	public final String hash;
	/** The total file size in bytes. */
	public final long size;
	
	// Progress
	
	/** How many bytes of the file we've downloaded. */
	public long saved; // Starts out 0, more added as it comes in
}
