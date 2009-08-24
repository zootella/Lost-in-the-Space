package lost.in.the.space;

import org.limewire.lifecycle.Service;
import org.limewire.lifecycle.ServiceStage;

import com.google.inject.Singleton;
import com.google.inject.Inject;

@Singleton public class BridgeGlue {
	
    private final BridgeService bridgeService;

    @Inject public BridgeGlue(BridgeService bridgeService) {
        this.bridgeService = bridgeService;
    }
    
    public BridgeService getBridgeService() { return bridgeService; }
    
    @Inject private void register(org.limewire.lifecycle.ServiceRegistry registry) {
        registry.register(new Service() {
        	
            public String getServiceName() { return "Bridge"; }
            public void initialize() {}
            public void start() { bridgeService.start(); }
            public void stop() { bridgeService.stop(); }
            public void restart() { bridgeService.restart(); }
            public boolean isAsyncStop() { return true; }
            
        }).in(ServiceStage.LATE);
    }
}
