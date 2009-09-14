package lost.in.the.space.cycle.pick;


import org.zootella.cheat.file.Name;

/** Sorts a File by the average length of its filenames. */
public class ByName implements Comparable<ByName> {
	
	public ByName(PickFile file) {
		this.file = file;
	}
	public final PickFile file;
	
	public int averageNameLength() {
		int total = 0;
		for (Name name : file.names)
			total += name.toString().length();
		return total / file.names.size();
	}
	
	@Override public int compareTo(ByName b) {
		return averageNameLength() - b.averageNameLength();
	}
	
	@Override public String toString() {
		return file.toString();
	}
}
