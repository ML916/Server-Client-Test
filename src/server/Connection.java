package server;

import model.Corridor;

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

    public Connection(Socket socket, Corridor corridor){
        this.socket = socket;
        this.corridor = corridor;
        this.pedestrianList = new ArrayList<>();
    }

    @Override
    public String call() throws Exception {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            synchronized (corridor){
                outputStream.writeObject(corridor);
            }
            inputStream = new ObjectInputStream(socket.getInputStream());
            pedestrianList = (ArrayList<Pedestrian>) inputStream.readObject();
            
            for(Pedestrian pedestrian : pedestrianList){
                if(corridor.pedestrianList.contains(pedestrian)){
                    corridor.pedestrianList.set(corridor.pedestrianList.indexOf(pedestrian), pedestrian);
                }
            }

            pedestrianList.clear();
            System.out.println("Call completed");
            fireCorridorChangeEvent();
            return Thread.currentThread().getName();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fireCorridorChangeEvent(){

    }
}
