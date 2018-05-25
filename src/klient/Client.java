package klient;

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

    public Client(){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 11111);
            System.out.println("Connection with server established");
            while(true) {
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                pedestrian = (Pedestrian) objectInputStream.readObject();
                //pedestrianList = (ArrayList<Pedestrian>) objectInputStream.readObject();
                //this.pedestrianList.forEach(Pedestrian::modifyObject);

                System.out.println("ID from received object:" + pedestrian.id + " Position: " + pedestrian.getPosition());
                pedestrian.modifyObject();
                //this.pedestrianList.forEach(Pedestrian::modifyObject);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(pedestrian);
                //objectOutputStream.writeObject(pedestrianList);
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
