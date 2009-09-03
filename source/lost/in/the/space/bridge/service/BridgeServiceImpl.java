package lost.in.the.space.bridge.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lost.in.the.space.bridge.my.MyLifecycleEventListener;
import lost.in.the.space.bridge.my.MySearchDetails;
import lost.in.the.space.bridge.my.MySearchListener;
import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.limewire.core.api.download.DownloadException;
import org.limewire.core.api.download.DownloadListManager;
import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchDetails;
import org.limewire.core.api.search.SearchFactory;
import org.limewire.core.api.search.SearchResult;
import org.limewire.core.impl.search.CoreSearch;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;
import com.limegroup.gnutella.library.FileCollectionManager;
import com.limegroup.gnutella.library.SharedFileCollection;

@Singleton public class BridgeServiceImpl implements BridgeService {
	
	// Inject

	@Inject public BridgeServiceImpl(
		LifecycleManager lifecycleManager,
		FileCollectionManager fileCollectionManager,
		SearchFactory searchFactory,
		DownloadListManager downloadListManager) {
		
		// Save references to injected parts of LimeWire
		this.lifecycleManager = lifecycleManager;
		this.fileCollectionManager = fileCollectionManager;
		this.searchFactory = searchFactory;
		this.downloadListManager = downloadListManager;

		// Connect to the Ford that lets us talk to the window above
		update = new Update(new MyReceive());
		update.send(); // There may already be a message waiting for us
		bridge = Bridge.instance();
		bridge.updateDown(update); // We want to find out when messages come down
		
		lifecycleManager.addListener(new MyLifecycleEventListener(bridge, update));
	}

	private final LifecycleManager lifecycleManager;
	private final FileCollectionManager fileCollectionManager;
	private final SearchFactory searchFactory;
	private final DownloadListManager downloadListManager;
	
	private final Update update;
	private final Bridge bridge;

	// Service
	    
	@Override public String getServiceName() {
		return org.limewire.i18n.I18nMarker.marktr("Bridge Service");
	}
	@Override public void start() {
		if (!running) {
			running = true;
		}
	}
	public void restart() {}
	public void stop() {
		running = false;
	}
	public Boolean isServerRunning() {
		return running;
	}
	private boolean running;

	// Sort

	// Java calls this when a new message was sent down for us
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			// Don't do messages until this service is running
			if (!running) return;

			// Loop for each message that has arrived from the ui above
			while (true) {
				JSONObject o = bridge.receiveDown();
				if (o == null)
					return; // No more messages right now
				
				// Do the message
				JSONObject r = message(o);
				if (r != null)
					bridge.sendUp(r); // Send a response
			}
		}
	}

	/** Do what o from the ui above tells us to do, and send a reply back up or null for none. */
	public JSONObject message(JSONObject o) {
		try {
			
			if      (o.has("quit"))   return quit(o);
			else if (o.has("share"))  return share(o);
			else if (o.has("search")) return search(o);
			else return Bridge.say("unknown");

		} catch (Exception e) { return Bridge.say("exception", e.toString()); }
	}
	
	// Do
	
	private JSONObject quit(JSONObject o) throws JSONException {
		lifecycleManager.shutdown();
		return null;
	}

	private JSONObject share(JSONObject o) throws JSONException {
		SharedFileCollection shared = fileCollectionManager.getCollectionById(0); // Index 0 gets the Gnutella public file list
		shared.add(new File(o.getString("share")));
		return null;
	}	
	
	private JSONObject search(JSONObject o) throws JSONException {
		String s = o.getString("search");

        SearchDetails details = new MySearchDetails(s);
        Search search = searchFactory.createSearch(details);
        search.addSearchListener(new MySearchListener(bridge));
		search.start();
		
		CoreSearch coreSearch = (CoreSearch)search;
		coreSearch.getQueryGuid();
		return Bridge.say("search", coreSearch.getQueryGuid().toString());
	}
	
	private JSONObject download(JSONObject o) throws JSONException, DownloadException, IOException {

		
		
		/*
		// find the search
		GUID guid = new GUID(o.getString("search_guid"));
		SearchWithResults search = searchManager.getSearchByGuid(guid);

		// find the search result
		URN urn = URN.createSHA1Urn(o.getString("result_urn"));
		List<SearchResult> list = new ArrayList<SearchResult>();
		for (SearchResult result : search.getSearchResults()) {
			if (result.getUrn().equals(urn)) {
				list.add(result);
			}
		}
		*/
		
		Search search = null;
		List<SearchResult> list = null;
		downloadListManager.addDownload(search, list);

		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
