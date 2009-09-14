package lost.in.the.space.cycle.pick;


public class BySize implements Comparable<BySize> {
	
	public BySize(PickFile file) {
		this.file = file;
	}
	public final PickFile file;

	@Override public int compareTo(BySize b) {
		long l = file.size - b.file.size;
		if (l < 0) return -1;
		if (l > 0) return 1; // Positive means this object is greater than b
		return 0;
	}
	
	@Override public String toString() {
		return file.toString();
	}
}
