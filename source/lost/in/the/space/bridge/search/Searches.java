package lost.in.the.space.bridge.search;

import java.util.ArrayList;
import java.util.List;

import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.impl.search.CoreSearch;
import org.limewire.io.GUID;

/** The program's Searches object keeps a list of the Search and SearchResult objects the core gives us. */
public class Searches {

	// Make
	
	/** Make a Searches object to hold on to the Search and SearchResult objects the core will give us. */
	public Searches() {
		searches = new ArrayList<SearchWithResults>();
	}
	private final List<SearchWithResults> searches;

	// Add

	/** Have this object hold on to a new Search object you just made. */ 
	public synchronized void add(Search search) {
		searches.add(new SearchWithResults(search));
	}

	/** Have this object hold on to a new SearchResult the core just told you about. */
	public synchronized void add(Search search, SearchResult result) {
		SearchWithResults w = find(search);
		if (w != null)
			w.add(result);
	}
	
	// Remove

	/** Stop the search with the given GUID and forget about it. */
	public synchronized void remove(GUID guid) {
		SearchWithResults w = find(guid);
		if (w != null) {
			searches.remove(w); // Remove it from our list
			w.search.stop(); // Tell the core we're finished with it
		}
	}

	// Find
	
	/** Find the given Search in our list. */
	private SearchWithResults find(Search search) {
		return find(((CoreSearch)search).getQueryGuid()); // Cast to CoreSearch to be able to call getQueryGuid()
	}

	/** Find the search with the given GUID in our list. */
	public synchronized SearchWithResults find(GUID guid) {
		for (SearchWithResults w : searches) {
			if (w.guid().equals(guid)) {
				return w;
			}
		}
		return null; // Not found
	}
}
