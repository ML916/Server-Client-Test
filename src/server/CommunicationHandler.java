package server;

import model.Corridor;
import model.CorridorListener;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunicationHandler extends Thread {

    private ArrayList<Connection> connections;
    private List<CorridorListener> listeners = new ArrayList<CorridorListener>();
    private ExecutorService threadPool;
    private Corridor corridor;
    private boolean isConnectionActive = true;

    public CommunicationHandler() {
        connections = new ArrayList<Connection>();
        threadPool = Executors.newCachedThreadPool();
        //this.start();
    }

    public CommunicationHandler(Corridor corridor){
        this();
        this.corridor = corridor;
    }

    public void toggleIsConnectionActive(){
        this.isConnectionActive = !isConnectionActive;
    }

    @Override
    public void run() {
        while(isConnectionActive) {
                try {
                    synchronized (connections) {
                        /*List<Future<String>> futures = */
                        threadPool.invokeAll(connections);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            fireCorridorChangeEvent();
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void fireCorridorChangeEvent(){
        synchronized (corridor){
            for (CorridorListener listener: listeners) {
                listener.onCorridorChange();
            }
        }
    }

    public void addConnection(Socket socket){
        synchronized (connections) {
            Connection connection;
            synchronized (corridor){
              connection = new Connection(socket, corridor);
            }
            connections.add(connection);

            //if (connections.size() > 2 && !this.isAlive())
            if(!this.isAlive())
                this.start();
        }
    }

    public void addListener(CorridorListener listener){
        listeners.add(listener);
    }
}
