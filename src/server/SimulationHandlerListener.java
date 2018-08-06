package server;

public interface SimulationHandlerListener {
    void onConnectionDropped();
    void onConnectionAccepted();
}
