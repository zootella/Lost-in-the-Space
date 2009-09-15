package lost.in.the.space.cycle.pick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zootella.cheat.file.Name;
import org.zootella.cheat.file.Path;

public class Pick {
	
	public Pick() {
		hashes = new HashSet<String>();
		paths = new HashSet<String>();
	}
	private final Set<String> hashes;
	private final Set<String> paths;
	
	public Path name(Path folder, Name name) {
		int i = 1;
		while (true) {
			Path path = folder.add(name.number(i));
			if (!paths.contains(path.toString().toLowerCase())) { // Not in our list
				paths.add(path.toString().toLowerCase());
				return path;
			}
			i++;
		}
	}

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

		// 2 points for more than one peer
		for (ResultFile file : results)
			if (file.peers.size() > 1)
				file.point(2);
		
		// 1 point for middle number of peers
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.BySize());
		mediocrity(results);

		// 1 point for middle size
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByPeer());
		mediocrity(results);
		
		// 1 point for middle name length
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByName());
		mediocrity(results);

		// Sort the winners to the top
		Collections.shuffle(results);
		Collections.sort(results, new ResultFile.ByPoint());

		List<ResultFile> pick = new ArrayList<ResultFile>();
		for (ResultFile file : results) {
			if (pick.size() >= 3) break;
			
			if (!hashes.contains(file.hash)) {
				hashes.add(file.hash);

				
				
				
//				file.path(path);
				
				
				pick.add(file);
			}
		}
		return pick;
	}
	
	/** Award a point to the middle third of the items in list. */
	private void mediocrity(List<ResultFile> list) {
		int i = 0;
		for (ResultFile file : list) {
			if (i > list.size() / 3 && i < 2 * list.size() / 3) {
				file.point(1);
			}
			i++;
		}
	}
}
