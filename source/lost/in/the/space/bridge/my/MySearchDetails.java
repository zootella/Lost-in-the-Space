package lost.in.the.space.bridge.my;

import java.util.Map;

import org.limewire.core.api.FilePropertyKey;
import org.limewire.core.api.search.SearchCategory;
import org.limewire.core.api.search.SearchDetails;

/** Make a SearchDetails object to fill out exactly what kind of Gnutella search we want. */
public class MySearchDetails implements SearchDetails {

	/** Bundle the given search keyword and media type category into a MySearchDetails to make a new Gnutella search. */
	public MySearchDetails(String query, SearchCategory category) {
		this.query = query; // Save everything
		this.category = category;
	}
	private final String query;
	private final SearchCategory category;
	
	@Override public SearchCategory getSearchCategory() { // LimeWire will call these methods later to read what's inside
		return category;
	}
	
	@Override public String getSearchQuery() {
		return query;
	}
	
	@Override public SearchType getSearchType() {
		return SearchType.KEYWORD;
	}
	
	@Override public Map<FilePropertyKey, String> getAdvancedDetails() {
		return null;
	}   
}
