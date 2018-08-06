package server;

public interface ServerListener {
    void onServerIsAlive();
    void onServerDisconnected();
}
