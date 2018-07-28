package server;

import model.Corridor;
import model.CorridorListener;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationHandler extends Thread {

    private ArrayList<Connection> connections;
    private List<CorridorListener> listeners = new ArrayList<CorridorListener>();
    private ExecutorService threadPool;
    private Corridor corridor;
    private boolean isSimulationActive = true;

    public SimulationHandler() {
        connections = new ArrayList<Connection>();
        threadPool = Executors.newCachedThreadPool();
    }

    public SimulationHandler(Corridor corridor){
        this();
        this.corridor = corridor;
    }

    public void toggleIsConnectionActive(){
        this.isSimulationActive = !isSimulationActive;
    }

    public boolean isSimulationActive() {
        return isSimulationActive;
    }

    @Override
    public void run() {
        int simulationTimer = 0;
        while(isSimulationActive && connections.size() > 0) {
            try {
                synchronized (connections) {
                    /*List<Future<String>> futures = */
                    threadPool.invokeAll(connections);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            fireCorridorChangeEvent();
            if (simulationTimer % 3 == 0)
                corridor.addNewPedestrian();
            //corridor.removePedestriansInGoalArea();
            try {
                this.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simulationTimer++;
        }
    }

    private void fireCorridorChangeEvent(){
            for (CorridorListener listener: listeners) {
                listener.onCorridorChange();
            }
    }

    /*private void firePedestriansRemovedEvent(){
        synchronized (corridor){
            for (CorridorListener listener: listeners){
                listener.onRemovedPedestrians();
            }
        }
    }*/

    public void addConnection(Socket socket){
        synchronized (connections) {
            Connection connection;
            synchronized (corridor){
              connection = new Connection(socket, corridor);
            }
            connections.add(connection);
        }
    }

    public void addListener(CorridorListener listener){
        listeners.add(listener);
    }
}
