package lost.in.the.space.bridge.add;

import java.io.File;
import java.io.IOException;

import lost.in.the.space.bridge.keep.SearchWithResults;
import lost.in.the.space.bridge.my.MyLifecycleEventListener;
import lost.in.the.space.bridge.my.MySearchDetails;
import lost.in.the.space.bridge.my.MySearchListener;
import lost.in.the.space.program.Bridge;

import org.json.JSONException;
import org.json.JSONObject;
import org.limewire.core.api.download.DownloadException;
import org.limewire.core.api.download.DownloadItem;
import org.limewire.core.api.download.DownloadListManager;
import org.limewire.core.api.download.DownloadItem.ErrorState;
import org.limewire.core.api.search.Search;
import org.limewire.core.api.search.SearchCategory;
import org.limewire.core.api.search.SearchDetails;
import org.limewire.core.api.search.SearchFactory;
import org.limewire.core.impl.search.CoreSearch;
import org.limewire.io.GUID;
import org.zootella.cheat.state.Receive;
import org.zootella.cheat.state.Update;

import ca.odell.glazedlists.EventList;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.limegroup.gnutella.LifecycleManager;
import com.limegroup.gnutella.URN;
import com.limegroup.gnutella.library.FileCollectionManager;
import com.limegroup.gnutella.library.SharedFileCollection;

/** BridgeServiceImpl is a new part we're adding to LimeWire to connect the bottom of Bridge to the LimeWire API beneath. */
@Singleton public class BridgeServiceImpl implements BridgeService {
	
	// Inject

	/** Make BridgeServiceImpl, a part we're adding to LimeWire at the bottom of the Bridge. */
	@Inject public BridgeServiceImpl( // Name the parts of LimeWire we want here, and Google Guice will magically inject them for us
		LifecycleManager lifecycleManager,
		FileCollectionManager fileCollectionManager,
		SearchFactory searchFactory,
		DownloadListManager downloadListManager) {
		
		this.lifecycleManager = lifecycleManager; // Save the references Guice injection gave us
		this.fileCollectionManager = fileCollectionManager;
		this.searchFactory = searchFactory;
		this.downloadListManager = downloadListManager;

		update = new Update(new MyReceive());
		update.send(); // There may already be a message waiting for us, look for it very soon
		bridge = Bridge.instance(); // Connect to the Bridge that lets us talk to the program above
		bridge.updateDown(update); // Sign up to find out when new messages come down here for us
		
		lifecycleManager.addListener(new MyLifecycleEventListener(bridge, update)); // Find out when LimeWire starts and stops
	}

	private final LifecycleManager lifecycleManager; // Parts of LimeWire Guice injected us access to
	private final FileCollectionManager fileCollectionManager;
	private final SearchFactory searchFactory;
	private final DownloadListManager downloadListManager;

	/** A link to the Bridge object that connects us to the program above. */
	private final Bridge bridge;
	/** Our Update object that code can call send() on when something we care about may be different. */
	private final Update update;

	// Service
	    
	@Override public String getServiceName() {
		return org.limewire.i18n.I18nMarker.marktr("Bridge Service");
	}
	@Override public void start() {
		if (!running)
			running = true;
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

	/** Java calls this very soon after the program sent a new message down the Bridge for us. */
	private class MyReceive implements Receive {
		@Override public void receive() throws Exception {
			
			if (!running) return; // Don't do messages until this service is running

			while (true) { // Loop for each message that has arrived from the program above
				JSONObject o = bridge.receiveDown();
				if (o == null)
					return; // No more messages right now
				
				JSONObject r = message(o); // Do the message
				if (r != null)
					bridge.sendUp(r); // Send the response back up
			}
		}
	}

	/** Do what message o from the program above tells us to do, and send a reply back up or null for none. */
	public JSONObject message(JSONObject o) {
		try {

			if      (o.has("quit"))     return quit(o); // Sort and do the given message
			else if (o.has("share"))    return share(o);
			else if (o.has("search"))   return search(o);
			else if (o.has("stop"))     return stop(o);
			else if (o.has("download")) return download(o);
			else if (o.has("progress")) return progress(o);
			else if (o.has("cancel"))   return cancel(o);
			else return Bridge.say("unknown"); // Unknown message

		} catch (Exception e) { return Bridge.say("exception", e.toString()); } // Return the exception
	}
	
	// Program
	
	/** Quit the program. */
	private JSONObject quit(JSONObject o) throws JSONException {
		lifecycleManager.shutdown(); // Later, the core will say "quitted" upwards
		return null;
	}

	// Share

	/** Share a file. */
	private JSONObject share(JSONObject o) throws JSONException {
		SharedFileCollection shared = fileCollectionManager.getCollectionById(0); // Index 0 gets the Gnutella public file list
		shared.add(new File(o.getString("share"))); // The value is the disk path to the file to share
		return null;
	}
	
	// Search

	/** Search Gnutella. */
	private JSONObject search(JSONObject o) throws JSONException {
		o = o.getJSONObject("search"); // Move down into the value
		
		String keyword = o.getString("keyword"); // Get the keyword the user wants to search for
		SearchCategory type = SearchCategory.ALL; // By default, search Gnutella for all types
		if (o.has("type")) { // If requested, narrow the Gnutella search to a specific type
			String t = o.getString("type");
			if (t.equals("audio"))    type = SearchCategory.AUDIO;
			if (t.equals("document")) type = SearchCategory.DOCUMENT;
			if (t.equals("image"))    type = SearchCategory.IMAGE;
			if (t.equals("video"))    type = SearchCategory.VIDEO;
		}

        SearchDetails details = new MySearchDetails(keyword, type);
        Search search = searchFactory.createSearch(details); // Make the Search
        search.addSearchListener(new MySearchListener(bridge)); // Sign up to get results as they arrive
		search.start(); // Start it
		
		bridge.searches.add(search); // Keep the Search object the core gave us, we can't ask for it later
		
		return Bridge.say("search", ((CoreSearch)search).getQueryGuid().toString()); // Send up the GUID we got
	}
	
	/** Stop a Gnutella search we previously started. */
	private JSONObject stop(JSONObject o) throws JSONException {
		String s = o.getString("stop");
		GUID g = new GUID(s);
		bridge.searches.remove(g); // Remove the Search from our list and tell the core to stop it
		return null;
	}
	
	// Download
	
	/** Download a file by its hash that we've seen in search results. */
	private JSONObject download(JSONObject o) throws JSONException, DownloadException, IOException {
		o = o.getJSONObject("download"); // Move down into the value
		
		GUID guid = new GUID(o.getString("guid")); // Parse values into objects
		URN hash = URN.createSHA1Urn(o.getString("hash"));
		File path = new File(o.getString("path"));

		SearchWithResults w = bridge.searches.find(guid); // Find the search
		if (w == null)
			return Bridge.say("not found", guid.toString());

		downloadListManager.addDownload( // Start a new download
			w.search,     // The Search object that found the file we want to download
			w.list(hash), // All the SearchResult objects that are the file we want to download
			path,         // The path to save it on the disk once it's done
			false);       // false to not overwrite, it's our job to pick a path that's available

		return null;
	}
	
	/** Find out how a download is progressing. */
	private JSONObject progress(JSONObject o) throws JSONException, IOException {
		URN hash = URN.createSHA1Urn(o.getString("progress")); // Find the download from the given file hash
		DownloadItem download = find(hash);
		if (download == null)
			return Bridge.say("not found", hash.toString());
		
		JSONObject p = new JSONObject(); // Get information about it
		p.put("hash", hash.toString()); // Same as the given hash
		p.put("saved", download.getCurrentSize()); // Number of bytes we've saved
		p.put("size", download.getTotalSize()); // Size of the entire file in bytes
		if (download.getErrorState() != ErrorState.NONE) // Only include "error" if there is one
			p.put("error", download.getErrorState().toString());
		
		JSONObject r = new JSONObject(); // Put that all under "progress" and send it up
		r.put("progress", p);
		return r;
	}

	/** Cancel a download. */
	private JSONObject cancel(JSONObject o) throws JSONException, IOException {
		URN hash = URN.createSHA1Urn(o.getString("cancel")); // Find the download from the given file hash
		DownloadItem download = find(hash);
		if (download == null)
			return Bridge.say("not found", hash.toString());
		
		download.cancel(); // Tell the core we don't want to download it anymore
		return null;
	}

	/** Given a file hash, find the DownloadItem object the core is using to download it from Gnutella. */
	private DownloadItem find(URN hash) {
		EventList<DownloadItem> downloads = downloadListManager.getSwingThreadSafeDownloads(); // No idea, but why not?
		for (DownloadItem download : downloads)
			if (download.getUrn().equals(hash))
				return download;
		return null; // Not found
	}
}
