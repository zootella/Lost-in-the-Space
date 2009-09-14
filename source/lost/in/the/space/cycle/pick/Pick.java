package lost.in.the.space.cycle.pick;

import java.util.Collections;
import java.util.List;

import org.zootella.cheat.file.Name;

public class Pick {

	public static void pick(List<ResultFile> results, String ext) {

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
	}
	
	/** Award a point to the middle third of the items in list. */
	private static void mediocrity(List<ResultFile> list) {
		int i = 0;
		for (ResultFile file : list) {
			if (i > list.size() / 3 && i < 2 * list.size() / 3) {
				file.point(1);
			}
			i++;
		}
	}
}
