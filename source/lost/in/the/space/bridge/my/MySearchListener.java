package lost.in.the.space.bridge.my;

import java.util.Collection;
import java.util.List;

import lost.in.the.space.program.Bridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.limewire.core.api.endpoint.RemoteHost;
import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchListener;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.api.search.sponsored.SponsoredResult;
import org.limewire.core.impl.search.CoreSearch;
import org.zootella.cheat.process.Mistake;

/** Implement SearchListener to listen in on all the Gnutella search results LimeWire's receiving. */
public class MySearchListener implements SearchListener {

	/** Make a MySearchListener to peek at all the Gnutella search results the program gets. */
	public MySearchListener(Bridge bridge) {
		this.bridge = bridge; // Save the given link to the Bridge between the LimeWire API and the program above
	}
	private final Bridge bridge;

	@Override public void searchStarted(Search search) {}
	@Override public void searchStopped(Search search) {}        
	@Override public void handleSponsoredResults(Search search, List<SponsoredResult> sponsoredResult) {} // Spam? No, thank you.

	@Override public void handleSearchResults(Search search, Collection<? extends SearchResult> searchResults) {
		for (SearchResult result : searchResults) // Loop for each SearchResult
			handleSearchResult(search, result); // And just hand it to the next method
	}
	
	/** When LimeWire gets a Gnutella search result, it calls this method. */
	@Override public void handleSearchResult(Search search, SearchResult result) {
		try {

			JSONArray a = new JSONArray(); // Make a new text message with information about the search result
			for (RemoteHost host : result.getSources())
				a.put(host.getFriendPresence().getFriend().getName()); // The IP address
			JSONObject o = new JSONObject();
			o.put("search", ((CoreSearch)search).getQueryGuid().toString());
			o.put("hash", result.getUrn());
			o.put("size", result.getSize());
			o.put("name", result.getFileName());
			o.put("ext", result.getFileExtension());
			o.put("peers", a);
			JSONObject p = new JSONObject();
			p.put("result", o);
			
			bridge.sendUp(p); // Send the message up to the program above
			bridge.searches.add(search, result); // Save the Search and SearchResult objects; we'll need them to download stuff later

		} catch (JSONException e) { Mistake.stop(e); }
	}
}