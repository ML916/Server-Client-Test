package listener_interfaces;

/**
 * An interface to listen for completed rounds of simulation
 * @see model.SimulationHandler
 */
public interface SimulationListener {
    void onSimulationRoundComplete();
}
