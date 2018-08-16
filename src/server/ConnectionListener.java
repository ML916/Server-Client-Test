package server;

public interface ConnectionListener {
    void onConnectionDropped();
    void onConnectionAccepted();
}
