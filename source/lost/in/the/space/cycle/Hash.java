package lost.in.the.space.cycle;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;
import org.zootella.cheat.net.name.Ip;

public class Hash {
	
	public static Hash parse(JSONObject o) {
		try {
			
			o = o.getJSONObject("result");
			
			String hash = o.getString("hash");
			long size = o.getLong("size");
			
			Hash h = new Hash(hash, size);
			
			return h;
			
			
			
			
		} catch (JSONException e) {}
		return null;
	}
	
	public Hash(String hash, long size) {
		this.hash = hash;
		this.size = size;
		names = new HashSet<Name>();
		peers = new HashSet<Ip>();
		guids = new HashSet<String>();
		
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
	public final Set<String> guids;
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
