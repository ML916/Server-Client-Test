package server;

import klient.SimulationClient;
import model.Corridor;
import model.SimulationListener;
import model.Pedestrian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static server.SimulationHandler.SimulationStatus.ACTIVE;
import static server.SimulationHandler.SimulationStatus.OFF;
import static server.SimulationHandler.SimulationStatus.PAUSED;

/**
 * Maintains the necessary communications with clients to run a simulation
 */
public class SimulationHandler extends Thread {

    private ArrayList<Connection> connections;
    private List<SimulationListener> simulationListeners = new ArrayList<SimulationListener>();
    private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
    private ExecutorService threadPool;
    private Corridor corridor;
    private SimulationStatus simulationStatus = PAUSED;
    private int simulationTimer;
    public final int REQUIRED_NUMBER_OF_CONNECTIONS;

    public SimulationHandler() {
        connections = new ArrayList<Connection>();
        threadPool = Executors.newCachedThreadPool();
        REQUIRED_NUMBER_OF_CONNECTIONS = 2;
        simulationTimer = 0;
    }

    public SimulationHandler(Corridor corridor){
        this();
        this.corridor = corridor;
    }

    public int getNumberOfConnections(){
        return connections.size();
    }

    public SimulationStatus getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(SimulationStatus simulationStatus) {
        this.simulationStatus = simulationStatus;
    }

    @Override
    public void run() {
        while(simulationStatus != OFF) {
            if(connections.size() < REQUIRED_NUMBER_OF_CONNECTIONS){
                simulationStatus = PAUSED;
            }
            if (simulationStatus == ACTIVE) {
                try {
                    List<Future<Boolean>> futures;
                    synchronized (connections) {
                        futures = threadPool.invokeAll(connections);
                    }
                    for (Future<Boolean> future : futures) {
                        future.get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                fireSimulationRoundCompletedEvent();
                if (simulationTimer % 3 == 0) {
                    corridor.addNewPedestrian();
                }

                try {
                    this.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simulationTimer++;
            }
        }
    }

    private void fireSimulationRoundCompletedEvent(){
            for (SimulationListener listener: simulationListeners) {
                listener.onSimulationRoundComplete();
            }
    }

    private void fireConnectionDroppedEvent(){
        for (ConnectionListener listener: connectionListeners){
            listener.onConnectionDropped();
        }
    }

    private void fireConnectionAcceptedEvent(){
        for (ConnectionListener listener: connectionListeners){
            listener.onConnectionAccepted();
        }
    }

    public void addConnection(Socket socket) {
        if (simulationStatus != OFF) {
            synchronized (connections) {
                synchronized (corridor) {
                    new Connection(socket);
                }
            }
            fireConnectionAcceptedEvent();
        }
    }

    public void addSimulationListener(SimulationListener listener){
        simulationListeners.add(listener);
    }

    public void addConnectionListener(ConnectionListener listener){
        connectionListeners.add(listener);
    }

    /**
     * Connection represents the connection with an individual SimulationClient and how it affects the simulation
     * @see SimulationClient
     */
    private class Connection implements Callable<Boolean> {
        private ArrayList<Pedestrian> pedestrianList;
        private Socket socket;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;

        public Connection(Socket socket){
            this.socket = socket;
            this.pedestrianList = new ArrayList<>();
            connections.add(this);
        }

        @Override
        public Boolean call() throws Exception {
            try {
                if (!socket.isClosed()) {
                    System.out.println("Call started");
                    outputStream = new ObjectOutputStream(socket.getOutputStream());
                    outputStream.writeInt(connections.indexOf(this) + 1);
                    outputStream.writeInt(connections.size());
                    synchronized (corridor) {
                        outputStream.writeObject(corridor);
                    }
                    inputStream = new ObjectInputStream(socket.getInputStream());
                    pedestrianList = (ArrayList<Pedestrian>) inputStream.readObject();

                    for (Pedestrian pedestrian : pedestrianList) {
                        corridor.editPedestrianInCorridor(pedestrian);
                    }

                    pedestrianList.clear();
                    System.out.println("Call completed");
                    return true;
                }
            } catch (IOException e) {
                terminateConnection();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                terminateConnection();
                e.printStackTrace();
            }
            return false;
        }

        private void terminateConnection() throws IOException {
            outputStream.close();
            inputStream.close();
            socket.close();
            connections.remove(this);
            fireConnectionDroppedEvent();
        }
    }

    public enum SimulationStatus {
        ACTIVE, PAUSED, OFF
    }
}
