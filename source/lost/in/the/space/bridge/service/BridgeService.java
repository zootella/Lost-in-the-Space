package lost.in.the.space.bridge.service;

public interface BridgeService {
    void start();
    void stop();
    String getServiceName();
    void restart();
}
