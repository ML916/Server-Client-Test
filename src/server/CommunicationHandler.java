package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommunicationHandler extends Thread {

    private ArrayList<Connection> connections;
    private ExecutorService threadPool;

    public CommunicationHandler() {
        connections = new ArrayList<Connection>();
        threadPool = Executors.newCachedThreadPool();
        //this.start();
    }

    @Override
    public void run() {
        while(true) {
            synchronized (connections) {
                for (Connection connection : this.connections) {
                    connection.run();
                }
                //List<Future<Connection>> futures = threadPool.invokeAll(connections);
            }
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addConnection(Socket socket){
        ArrayList<Pedestrian> list = new ArrayList<Pedestrian>();
        for(int i = 0; i < 3; i++){
            Pedestrian pedestrian = new Pedestrian();
            list.add(pedestrian);
        }
        synchronized (connections) {
            Connection connection = new Connection(socket, list);
            connections.add(connection);
            //threadPool.submit(connection);

            if (connections.size() > 2 && !this.isAlive())
                this.start();
        }
    }
}
