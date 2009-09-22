package lost.in.the.space.cycle.pick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;

/** The program's Pick object keeps track of tried hashes and used paths, and picks what to download next. */
public class Pick {
	
	// Object

	/** Make the program's Pick object. */
	public Pick() {
		hashes = new HashSet<String>(); // Make empty lists to hold things, use Set to block identical duplicates
		paths = new HashSet<String>();
	}
	/** The SHA1 file hashes that the program has already downloaded or tried to download. */
	private final Set<String> hashes;
	/** The absolute disk paths that the program has already told the LimeWire API to use. */
	private final Set<String> paths;
	
	// Use

	/** Given a list of new search results and the file extension the user typed, pick 3 new files to download. */
	public List<ResultFile> pick(List<ResultFile> results, String ext) {

		// 2 points for matching extension
		for (ResultFile file : results) {
			for (Name name : file.names) {
				if (name.extension.equalsIgnoreCase(ext)) {
					file.point(2);
					break;
				}
			}
		}

		// 2 points for having more than one peer who can give us the file
		for (ResultFile file : results)
			if (file.peers.size() > 1)
				file.point(2);
		
		// 1 point for middle number of peers, too many is likely spam, too few is likely to work
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByPeer());
		mediocrity(results);

		// 1 point for middle size
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.BySize());
		mediocrity(results);
		
		// 1 point for middle name length, exact short is spam, too long matches everything
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByName());
		mediocrity(results);

		// Sort the winners to the top
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByPoint());

		// Pick 3 we haven't tried before
		List<ResultFile> pick = new ArrayList<ResultFile>();
		for (ResultFile file : results) {
			if (pick.size() >= 3) break; // Stop when we've got 3
			
			if (!hashes.contains(file.hash)) { // A unique hash
				hashes.add(file.hash); // Save it in our list to avoid it in the future
				pick.add(file); // Add it to the list we'll return
			}
		}
		return pick; // Return the list of up to 3 files we picked
	}
	
	/** Award a point to the middle third of the items in the given list. */
	private void mediocrity(List<ResultFile> list) {
		int i = 0;
		for (ResultFile file : list) { // Loop through the whole list
			if (i > list.size() / 3 && i < 2 * list.size() / 3) { // We're in the middle third
				file.point(1);
			}
			i++;
		}
	}

	/** Given a folder a file name, pick an absolute path the LimeWire API probably isn't already using. */
	public Path name(Path folder, Name name) {
		int i = 1;
		while (true) { // Loop until we find something available
			Path path = folder.add(name.number(i));
			if (!paths.contains(path.toString().toLowerCase())) { // Not in our list
				paths.add(path.toString().toLowerCase());
				return path;
			}
			i++;
		}
	}
	
	// Help
	
	/** Given a file extension from the user, name the Gnutella search media type it's a part of, "" if none. */
	public static String extension(String ext) {
		ext = ext.toLowerCase(); // Make it lower case so it will match what we've typed here
		
		if (ext.equals("mp3"))  return "audio"; // Common audio file extensions
		if (ext.equals("ogg"))  return "audio";
		if (ext.equals("fla"))  return "audio";
		if (ext.equals("flac")) return "audio";

		if (ext.equals("txt"))  return "document"; // Documents
		if (ext.equals("pdf"))  return "document";
		if (ext.equals("pst"))  return "document";
		if (ext.equals("rtf"))  return "document";
		if (ext.equals("doc"))  return "document";
		if (ext.equals("docx")) return "document";
		if (ext.equals("xls"))  return "document";
		if (ext.equals("xlsx")) return "document";
		if (ext.equals("ppt"))  return "document";
		if (ext.equals("pptx")) return "document";

		if (ext.equals("jpg"))  return "image";
		if (ext.equals("jpeg")) return "image";
		if (ext.equals("png"))  return "image";
		if (ext.equals("gif"))  return "image";

		if (ext.equals("avi"))  return "video";
		if (ext.equals("mpg"))  return "video";
		if (ext.equals("mpeg")) return "video";
		if (ext.equals("flv"))  return "video";
		if (ext.equals("mov"))  return "video";
		if (ext.equals("m4v"))  return "video";

		return ""; // No common match, searching Gnutella for all types will be fine
	}
}
