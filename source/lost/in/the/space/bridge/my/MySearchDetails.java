package lost.in.the.space.bridge.my;

import java.util.Map;

import org.limewire.core.api.FilePropertyKey;
import org.limewire.core.api.search.SearchCategory;
import org.limewire.core.api.search.SearchDetails;

public class MySearchDetails implements SearchDetails {

	public MySearchDetails(String query) {
		this.query = query;
	}
	private final String query;
	
	@Override public SearchCategory getSearchCategory() {
		return SearchCategory.ALL;
	}
	
	@Override public String getSearchQuery() {
		return this.query;
	}
	
	@Override public SearchType getSearchType() {
		return SearchType.KEYWORD;
	}
	
	@Override public Map<FilePropertyKey, String> getAdvancedDetails() {
		return null;
	}   
}
