package lost.in.the.space.bridge;

public interface BridgeService {
    void start();
    void stop();
    String getServiceName();
    void restart();
}
