package lost.in.the.space.bridge.my;

import java.util.Collection;
import java.util.List;

import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchListener;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.api.search.sponsored.SponsoredResult;
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
    	for (SearchResult searchResult : searchResults) {
    		handleSearchResult(search, searchResult);
    	}
    }
    @Override public void handleSearchResult(Search search, SearchResult searchResult) {
    	try {

    		JSONObject o = new JSONObject();
    		o.put("hash", searchResult.getUrn());
    		o.put("size", searchResult.getSize());
    		o.put("name", searchResult.getFileName());
    		o.put("ext", searchResult.getFileExtension());
    		o.put("peers", searchResult.getSources().size());
    		bridge.sendUp(o);

    	} catch (JSONException e) { Mistake.stop(e); }
    }
}