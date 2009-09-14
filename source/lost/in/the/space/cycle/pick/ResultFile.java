package lost.in.the.space.cycle.pick;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.net.name.Ip;

public class ResultFile {
	
	public static ResultFile parse(JSONObject o) {
		try {
			o = o.getJSONObject("result");
			
			ResultFile file = new ResultFile(o.getString("hash"), o.getLong("size"));
			file.searches.add(o.getString("search"));
			file.names.add(new Name(o.getString("name")));
			
			JSONArray a = o.getJSONArray("peers");
			for (int i = 0; i < a.length(); i++)
				file.peers.add(new Ip(a.getString(i)));

			return file;

		} catch (JSONException e) {}
		return null;
	}
	
	public ResultFile(String hash, long size) {
		this.hash = hash;
		this.size = size;
		names = new HashSet<Name>();
		peers = new HashSet<Ip>();
		searches = new HashSet<String>();
		
	}
	
	/** The SHA1 hash of this file. */
	public final String hash;
	/** The size of the file in bytes. */
	public final long size;
	/** All the names different Gnutella peers have given this file. */
	public final Set<Name> names;
	/** The IP addresses of the Gnutella peers that are sharing this file. */
	public final Set<Ip> peers;
	/** The GUIDs of the searches that told us where this file is. */
	public final Set<String> searches;
	
	public void point(int p) { points += p; }
	private int points;
	
	public void add(Name name) {
		names.add(name);
	}
	public void add(Ip ip) {
		peers.add(ip);
	}
	
	/** Copy f's names, peers, and searches into this File object. */
	public void add(ResultFile f) {
		if (!hash.equals(f.hash)) throw new IllegalArgumentException();
		names.addAll(f.names);
		peers.addAll(f.peers);
		searches.addAll(f.searches);
	}
	
	// Sort
	
	public static class BySize implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			long l = a.size - b.size;
			if (l < 0) return -1;
			if (l > 0) return 1;
			return 0;
		}
	}
	
	public static class ByPeer implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return a.peers.size() - b.peers.size();
		}
	}

	/** Sorts a File by the average length of its filenames. */
	public static class ByName implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return a.averageNameLength() - b.averageNameLength();
		}
	}

	/** Sorts a File by the average length of its filenames. */
	public static class ByPoint implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return b.points - a.points; // b - a so the most points are at the top
		}
	}
	
	public int averageNameLength() {
		int total = 0;
		for (Name name : names)
			total += name.toString().length();
		return total / names.size();
	}
	
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\n");
		for (String s : searches)
			b.append(s + "\n");
		b.append(hash + " " + size + " bytes, " + points + " points\n");
		for (Name n : names)
			b.append("  " + n.toString() + "\n");
		b.append(" ");
		for (Ip i : peers)
			b.append(" " + i.toString());
		return b.toString();
	}
	
	

	

}
