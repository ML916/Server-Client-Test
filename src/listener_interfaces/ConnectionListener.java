package listener_interfaces;

/**
 * ConnectionListener listens for changes on the list of connections within the SimulationHandler
 * @see model.SimulationHandler
 */
public interface ConnectionListener {
    void onConnectionDropped();
    void onConnectionAccepted();
}
