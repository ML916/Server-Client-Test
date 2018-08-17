package listener_interfaces;

public interface ConnectionListener {
    void onConnectionDropped();
    void onConnectionAccepted();
}
