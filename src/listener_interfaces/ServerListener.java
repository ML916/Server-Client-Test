package listener_interfaces;

/**
 * Listens for changes on the servers status
 * @see server.Server
 */
public interface ServerListener {
    void onServerIsAlive();
    void onServerDisconnected();
}
