package server;

import model.Corridor;
import model.CorridorListener;
import model.Pedestrian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class SimulationHandler extends Thread {

    private ArrayList<Connection> connections;
    private List<CorridorListener> corridorListeners = new ArrayList<CorridorListener>();
    private List<SimulationHandlerListener> simulationHandlerListeners = new ArrayList<SimulationHandlerListener>();
    private ExecutorService threadPool;
    private Corridor corridor;
    private boolean isSimulationActive = false;

    public SimulationHandler() {
        connections = new ArrayList<Connection>();
        threadPool = Executors.newCachedThreadPool();
    }

    public SimulationHandler(Corridor corridor){
        this();
        this.corridor = corridor;
    }

    public void setIsSimulationActive(boolean isActive){
        isSimulationActive = isActive;
    }

    public boolean isSimulationActive() {
        return isSimulationActive;
    }

    @Override
    public void run() {
        int simulationTimer = 0;
        while(true) {
            while (isSimulationActive && connections.size() > 0) {
                try {
                    List<Future<String>> futures;
                    synchronized (connections) {
                        futures = threadPool.invokeAll(connections);
                    }
                    for (Future<String> future : futures) {
                        future.get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                fireCorridorChangeEvent();

                if (simulationTimer % 5 == 0)
                    corridor.addNewPedestrian();
                try {
                    this.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                simulationTimer++;
            }
        }
    }

    private void fireCorridorChangeEvent(){
            for (CorridorListener listener: corridorListeners) {
                listener.onCorridorChange();
            }
    }

    private void fireConnectionDroppedEvent(){
        for (SimulationHandlerListener listener: simulationHandlerListeners){
            listener.onConnectionDropped();
        }
    }

    private void fireConnectionAcceptedEvent(){
        for (SimulationHandlerListener listener: simulationHandlerListeners){
            listener.onConnectionAccepted();
        }
    }

    public void addConnection(Socket socket){
        synchronized (connections) {
            synchronized (corridor){
                new Connection(socket);
            }
        }
        fireConnectionAcceptedEvent();
    }

    public void addCorridorListener(CorridorListener listener){
        corridorListeners.add(listener);
    }

    public void addSimulationHandlerListener(SimulationHandlerListener listener){
        simulationHandlerListeners.add(listener);
    }

    private class Connection implements Callable<String> {
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
        public String call() throws Exception {
            try {
                if (!socket.isClosed()) {
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
                    return Thread.currentThread().getName();
                }
            } catch (IOException e) {
                terminateConnection();
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                terminateConnection();
                e.printStackTrace();
            }
            return null;
        }

        private void terminateConnection() throws IOException {
            outputStream.close();
            inputStream.close();
            socket.close();
            connections.remove(this);
            fireConnectionDroppedEvent();
        }
    }

}
