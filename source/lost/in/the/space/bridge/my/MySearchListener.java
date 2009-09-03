package lost.in.the.space.bridge.my;

import java.util.Collection;
import java.util.List;

import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchListener;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.api.search.sponsored.SponsoredResult;

public class MySearchListener implements SearchListener {
    
    public MySearchListener() {}
    
    @Override public void searchStarted(Search search) {}
    @Override public void searchStopped(Search search) {}        
    @Override public void handleSponsoredResults(Search search, List<SponsoredResult> sponsoredResult) {} // Spam? No, thank you.
    
    @Override public void handleSearchResults(Search search, Collection<? extends SearchResult> searchResults) {
    	for (SearchResult searchResult : searchResults) {
    		handleSearchResult(search, searchResult);
    	}
    }
    @Override public void handleSearchResult(Search search, SearchResult searchResult) {
    	
    	System.out.println("search result: " + searchResult.toString());
    }
}