package model;

import dataPacket.DataPacket;
import simulation_client.SimulationClient;
import listener_interfaces.SimulationListener;
import listener_interfaces.ConnectionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static model.SimulationHandler.SimulationStatus.ACTIVE;
import static model.SimulationHandler.SimulationStatus.OFF;
import static model.SimulationHandler.SimulationStatus.PAUSED;

/**
 * Maintains the necessary communications with clients to run a simulation
 */
public class SimulationHandler extends Thread {

    private ArrayList<Connection> connections;
    private List<SimulationListener> simulationListeners = new ArrayList<SimulationListener>();
    private List<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();
    private ExecutorService threadPool;
    public Corridor corridor;
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

    public void resetSimulation(){
        simulationTimer = 0;
        corridor.initPedestrianList();
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
        while(true) {
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
                simulationTimer++;
            }
            try {
                this.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fireSimulationRoundCompletedEvent(){
            System.out.println("Simulation round completed event!");
            for (SimulationListener listener: simulationListeners) {
                listener.onSimulationRoundComplete();
            }
    }

    private void fireConnectionDroppedEvent(){
        System.out.println("Connection dropped event");
        for (ConnectionListener listener: connectionListeners){
            listener.onConnectionDropped();
        }
    }

    private void fireConnectionAcceptedEvent(){
        System.out.println("Connection accepted event");
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
            try {
                this.outputStream = new ObjectOutputStream(socket.getOutputStream());
                this.inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.pedestrianList = new ArrayList<>();
            connections.add(this);
        }

        @Override
        public Boolean call() throws Exception {
            try {
                if (!socket.isClosed()) {
                    //System.out.println("Call started");

                    DataPacket dataPacket = new DataPacket(corridor,connections.indexOf(this) + 1,
                            connections.size(), false);
                    outputStream.writeObject(dataPacket);
                    /*synchronized (corridor) {
                        outputStream.writeObject(corridor);
                    }*/
                    pedestrianList = (ArrayList<Pedestrian>) inputStream.readObject();

                    //System.out.println("Index of connection: " + (connections.indexOf(this) + 1));
                    //System.out.println("Pedestrian list size: "+ pedestrianList.size());
                    for (Pedestrian pedestrian : pedestrianList) {
                        corridor.editPedestrianInCorridor(pedestrian);
                    }

                    pedestrianList.clear();
                    //System.out.println("Call completed");
                    return true;
                }
                else{
                    terminateConnection();
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
            synchronized (connections) {
                connections.remove(this);
            }
            outputStream.close();
            inputStream.close();
            socket.close();
            fireConnectionDroppedEvent();
            System.out.println("Connection terminated");
        }
    }

    public enum SimulationStatus {
        ACTIVE, PAUSED, OFF
    }
}
