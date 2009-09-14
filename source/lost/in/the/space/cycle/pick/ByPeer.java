package lost.in.the.space.cycle.pick;


public class ByPeer implements Comparable<ByPeer> {
	
	public ByPeer(PickFile file) {
		this.file = file;
	}
	public final PickFile file;

	@Override public int compareTo(ByPeer b) {
		return file.peers.size() - b.file.peers.size();
	}
	
	@Override public String toString() {
		return file.toString();
	}
}
