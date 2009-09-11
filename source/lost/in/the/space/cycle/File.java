package lost.in.the.space.cycle;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.net.name.Ip;

public class File {
	
	public static File parse(JSONObject o) {
		try {
			o = o.getJSONObject("result");
			
			File file = new File(o.getString("hash"), o.getLong("size"));
			file.searches.add(o.getString("search"));
			file.names.add(new Name(o.getString("name")));
			
			JSONArray a = o.getJSONArray("peers");
			for (int i = 0; i < a.length(); i++)
				file.peers.add(new Ip(a.getString(i)));

			return file;

		} catch (JSONException e) {}
		return null;
	}
	
	public File(String hash, long size) {
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
	/** The GUIDs of the searches that told us where ths file is. */
	public final Set<String> searches;
	/** The path where we saved this file, null before we've downloaded it. */
	public Path path() { return path; }
	public Path path;
	
	public void add(Name name) {
		names.add(name);
	}
	public void add(Ip ip) {
		peers.add(ip);
	}
	
	//TODO add the same name and ip several times, make sure the Set only keeps each unique one once
	
	

}
