package lost.in.the.space.bridge.my;

import java.util.Map;

import org.limewire.core.api.FilePropertyKey;
import org.limewire.core.api.search.SearchCategory;
import org.limewire.core.api.search.SearchDetails;

public class MySearchDetails implements SearchDetails {

	public MySearchDetails(String query, SearchCategory category) {
		this.query = query;
		this.category = category;
	}
	private final String query;
	private final SearchCategory category;
	
	@Override public SearchCategory getSearchCategory() {
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
