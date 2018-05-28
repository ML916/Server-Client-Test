package server;

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

    public Connection(Socket socket, ArrayList<Pedestrian> list){
        this.socket = socket;
        this.pedestrianList = list;
    }

    @Override
    public String call() throws Exception {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            for (Pedestrian p: this.pedestrianList) {
                System.out.println("Sending object: ID: " + p.id + " Pos: "+ p.getPosition());
            }
            outputStream.writeObject(pedestrianList);
            inputStream = new ObjectInputStream(socket.getInputStream());
            pedestrianList = (ArrayList<Pedestrian>) inputStream.readObject();
            return Thread.currentThread().getName();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
