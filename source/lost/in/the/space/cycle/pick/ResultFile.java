package lost.in.the.space.cycle.pick;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.net.name.Ip;

/** A ResultFile object holds information about a file with a unique SHA1 hash that one or more Gnutella search results have told us about. */
public class ResultFile {
	
	// Object

	/** Parse a "result" message from the LimeWire API into a new ResultFile object, or return null. */
	public static ResultFile parse(JSONObject o) {
		try {
			o = o.getJSONObject("result"); // Move beneath the "result" heading
			
			ResultFile file = new ResultFile(o.getString("search"), o.getString("hash"), o.getLong("size")); // New object
			file.names.add(new Name(o.getString("name"))); // Merge in all the file names different peers gave it
			
			JSONArray a = o.getJSONArray("peers");
			for (int i = 0; i < a.length(); i++) // Merge in all the IP addresses of peers that are sharing it
				file.peers.add(new Ip(a.getString(i)));

			return file; // Mission accomplished
		} catch (JSONException e) {} // Whoops, o wasn't formed the way we thought it would be
		return null; // Give up, that's cool too
	}

	/** Make a new ResultFile to keep together what different search results tell us about a file with a unique SHA1 hash. */
	private ResultFile(String search, String hash, long size) {
		this.search = search; // Save the GUID of the Gnutella search that introduced us to this file
		this.hash = hash; // The SHA1 hash that uniquely identifies the file
		this.size = size; // The file size in bytes, unique like the hash
		names = new HashSet<Name>(); // Make new empty lists
		peers = new HashSet<Ip>();
	}
	
	/** The GUID of the search that told us where this file is. */
	public final String search;
	/** The SHA1 hash of this file. */
	public final String hash;
	/** The size of the file in bytes. */
	public final long size;
	/** All the names different Gnutella peers have given this file. */
	public final Set<Name> names; // A Set keeps out duplicates
	/** The IP addresses of the Gnutella peers that are sharing this file. */
	public final Set<Ip> peers;

	/** Award this ResultFile p points for average behaviour. */
	public void point(int p) { points += p; }
	/** How many points this ResultFile has, more is better. */
	private int points;
	
	// Merge

	/** Copy f's names and peers into this File object. */
	public void add(ResultFile f) {
		if (!hash.equals(f.hash)) throw new IllegalArgumentException(); // Make sure the hashes identify the same file
		names.addAll(f.names); // Alternative names Gnutella peers have for this file
		peers.addAll(f.peers); // The IP addresses of additional peers we've found sharing this file
	}
	
	// Sort
	
	/** Use a BySize object to sort a list of ResultFile objects by their file size in bytes. */
	public static class BySize implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			long l = a.size - b.size; // Have to return an int, so can't just return l
			if (l < 0) return -1;
			if (l > 0) return 1;
			return 0;
		}
	}

	/** Use a ByPeer object to sort a list of ResultFile objects by how many unique peers we know to download the file from. */
	public static class ByPeer implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return a.peers.size() - b.peers.size();
		}
	}
	
	/** Use a ByName object to sort a list of ResultFile objects by how long their file names are, shortest names at the top, longest at the bottom. */
	public static class ByName implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return a.averageNameLength() - b.averageNameLength();
		}
	}

	/** Use a ByPoint object to sort a list of ResultFile objects by how many points they have, most points at the top. */
	public static class ByPoint implements Comparator<ResultFile> {
		@Override public int compare(ResultFile a, ResultFile b) {
			return b.points - a.points; // b - a so the most points are at the top, backwards of how size does it above
		}
	}
	
	// Help

	/** A single ResultFile can have any number of alternative file names, calculate the average length of this one. */
	public int averageNameLength() {
		int total = 0;
		for (Name name : names)
			total += name.toString().length();
		return total / names.size();
	}
	
	/** Turn this ResultFile object into some text for the programmer. */
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("\n");
		b.append(search  + " " + hash + " " + size + " bytes, " + points + " points\n");
		for (Name n : names)
			b.append("  " + n.toString() + "\n");
		b.append(" ");
		for (Ip i : peers)
			b.append(" " + i.toString());
		return b.toString();
	}
}
