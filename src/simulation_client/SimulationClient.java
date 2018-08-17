package simulation_client;

import model.Corridor;
import model.Pedestrian;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Runs the necessary calculations for simulation within a certain segment of the corridor
 * @see Corridor
 */
public class SimulationClient {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ArrayList<Pedestrian> pedestrianList;
    private Corridor corridor;
    private int connectionID;
    private int numberOfActiveConnections;

    public SimulationClient(){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 11111);
            System.out.println("Connection with server established");
            pedestrianList = new ArrayList<>();
            while(!socket.isClosed()) {
                System.out.println("Start of loop");
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                connectionID = objectInputStream.readInt();
                numberOfActiveConnections = objectInputStream.readInt();
                System.out.println("Connection ID: " + connectionID);
                System.out.println("Number of active connections: " + numberOfActiveConnections);
                corridor = (Corridor) objectInputStream.readObject();

                double startOfClientsMapSegment = (connectionID - 1)*corridor.getWidth()/numberOfActiveConnections;
                double endOfClientsMapSegment = (connectionID*corridor.getWidth()/numberOfActiveConnections);
                pedestrianList = corridor.pedestriansMovedWithinSegment(startOfClientsMapSegment, endOfClientsMapSegment);

                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(pedestrianList);
                pedestrianList.clear();
                System.out.println("End of loop");
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
        new SimulationClient();
    }
}
