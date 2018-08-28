package simulation_client;

import dataPacket.DataPacket;
import model.Corridor;
import model.Pedestrian;

import javax.xml.crypto.Data;
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
public class SimulationClient extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ArrayList<Pedestrian> pedestrianList;
    private Corridor corridor;
    private int connectionID;
    private int numberOfActiveConnections;

    /**
     * Creates a SimulationClient object and connects it to a socket using the InetAddress of the server
     */
    private SimulationClient(){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 11111);
            System.out.println("Connection with server established");
            pedestrianList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receives a DataPacket from the server containing a corridor object and info needed to determine which segment of the corridor it should work with. The pedestrians within the segment are modified and then sent back to the server in a list.
     */
    @Override
    public void run() {
        try{
            while(!socket.isClosed()){
                System.out.println("Start of loop");
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                DataPacket dataPacket = (DataPacket) objectInputStream.readObject();
                corridor = dataPacket.corridor;
                connectionID = dataPacket.connectionNumber;
                numberOfActiveConnections = dataPacket.numberOfActiveConnections;
                double startOfClientsMapSegment = (connectionID - 1) * corridor.getWidth() / numberOfActiveConnections;
                double endOfClientsMapSegment = (connectionID * corridor.getWidth() / numberOfActiveConnections);
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
        }finally {
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
        new SimulationClient().start();
    }
}
