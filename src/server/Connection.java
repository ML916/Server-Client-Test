package server;

import model.Corridor;
import model.Pedestrian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class Connection implements Callable<String> {
    private ArrayList<Pedestrian> pedestrianList;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Corridor corridor;
    public static int numberOfConnections = 0;
    public final int ID;

    public Connection(Socket socket, Corridor corridor){
        this.socket = socket;
        this.corridor = corridor;
        this.pedestrianList = new ArrayList<>();
        numberOfConnections++;
        ID = numberOfConnections;
    }

    @Override
    public String call() throws Exception {
        try {
            if (socket.isConnected()) {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeInt(ID);
                outputStream.writeInt(numberOfConnections);
                synchronized (corridor) {
                    outputStream.writeObject(corridor);
                }
                inputStream = new ObjectInputStream(socket.getInputStream());
                pedestrianList = (ArrayList<Pedestrian>) inputStream.readObject();

                for (Pedestrian pedestrian : pedestrianList) {
                    if (corridor.pedestrianList.contains(pedestrian)) {
                        corridor.pedestrianList.set(corridor.pedestrianList.indexOf(pedestrian), pedestrian);
                    }
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

    public void terminateConnection() throws IOException {
        numberOfConnections--;
        socket.close();
        outputStream.close();
        inputStream.close();
    }
}
