package klient;

import model.Corridor;
import server.Pedestrian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Pedestrian pedestrian;
    private ArrayList<Pedestrian> pedestrianList;
    private Corridor corridor;

    public Client(){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 11111);
            System.out.println("Connection with server established");
            pedestrianList = new ArrayList<>();
            while(true) {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                corridor = (Corridor) objectInputStream.readObject();

                for (Pedestrian p: corridor.getPedestrianList()) {
                    p.move(corridor);
                    pedestrianList.add(p);
                }

                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(pedestrianList);
                pedestrianList.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new Client();
    }
}
