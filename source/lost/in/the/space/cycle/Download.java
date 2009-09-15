package lost.in.the.space.cycle;


public class Download {
	
	public Download(String hash, long size) {
		this.hash = hash;
		this.size = size;
	}
	public final String hash;
	public final long size;
	
	public long saved;
	
	
}
