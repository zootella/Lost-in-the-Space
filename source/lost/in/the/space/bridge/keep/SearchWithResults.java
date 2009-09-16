package lost.in.the.space.bridge.keep;

import java.util.ArrayList;
import java.util.List;

import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.impl.search.CoreSearch;
import org.limewire.io.GUID;

import com.limegroup.gnutella.URN;

/** A SearchWithResults object holds a Search and keeps the SearchResult objects that arrive for it. */
public class SearchWithResults {
	
	// Make

	/** Wrap the given Search into a new SearchWithResults object. */
	public SearchWithResults(Search search) {
		this.search = search; // Save it
		results = new ArrayList<SearchResult>(); // Make a new empty list to hold the results that will come in
	}
	
	// Inside
	
	/** The Search object inside that is the Gnutella search in the core. */
	public final Search search; // Doesn't need to be synchronized because it's final
	/** A List of the SearchResult objects that came up from the core for the Search. */
	private final List<SearchResult> results;
	
	// Use
	
	/** The GUID that uniquely identifies this search in the program and on Gnutella. */
	public GUID guid() {
		return ((CoreSearch)search).getQueryGuid(); // Cast to CoreSearch to be able to call getQueryGuid()
	}

	/** Keep a SearchResult for our search in this object when the core gives it to us. */
	public synchronized void add(SearchResult result) {
		results.add(result); // Add it to our list of them
	}

	/** Given a file hash, get all the SearchResult objects that are for that file. */
	public synchronized List<SearchResult> list(URN hash) {
		List<SearchResult> list = new ArrayList<SearchResult>(); // Make a new empty List to fill and return
		for (SearchResult result : results) // Loop through our List of SearchResult objects
			if (result.getUrn().equals(hash)) // This SearchResult has the same hash
				list.add(result); // Add it to the list we'll return
		return list;
	}
}
