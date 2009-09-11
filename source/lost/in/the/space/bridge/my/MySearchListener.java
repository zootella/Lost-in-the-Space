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

public class MySearchListener implements SearchListener {

	public MySearchListener(Bridge bridge) {
		this.bridge = bridge;
	}
	private final Bridge bridge;

	@Override public void searchStarted(Search search) {}
	@Override public void searchStopped(Search search) {}        
	@Override public void handleSponsoredResults(Search search, List<SponsoredResult> sponsoredResult) {} // Spam? No, thank you.

	@Override public void handleSearchResults(Search search, Collection<? extends SearchResult> searchResults) {
		for (SearchResult result : searchResults) {
			handleSearchResult(search, result);
		}
	}
	@Override public void handleSearchResult(Search search, SearchResult result) {
		try {

			JSONArray a = new JSONArray();
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
			bridge.sendUp(p);
			
			bridge.searches.add(search, result);

		} catch (JSONException e) { Mistake.stop(e); }
	}
}