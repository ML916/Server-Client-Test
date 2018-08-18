package simulation_client;

import dataPacket.DataPacket;
import model.Corridor;
import model.Pedestrian;

import java.awt.desktop.SystemSleepEvent;
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

    public SimulationClient(){
        try {
            InetAddress address = InetAddress.getByName("localhost");
            socket = new Socket(address, 11111);
            System.out.println("Connection with server established");
            ArrayList<Pedestrian> pedestrianList = new ArrayList<>();

            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            while(!socket.isClosed()) {
                DataPacket dataPacket = (DataPacket) objectInputStream.readObject();
                Corridor corridor = dataPacket.corridor;
                int connectionID = dataPacket.connectionNumber;
                int numberOfActiveConnections = dataPacket.numberOfActiveConnections;
                double startOfClientsMapSegment = (connectionID - 1)* corridor.getWidth()/ numberOfActiveConnections;
                System.out.println("Start of segment: " + startOfClientsMapSegment);
                double endOfClientsMapSegment = (connectionID * corridor.getWidth()/ numberOfActiveConnections);
                System.out.println("End of segment: " + endOfClientsMapSegment);
                pedestrianList = corridor.pedestriansMovedWithinSegment(startOfClientsMapSegment, endOfClientsMapSegment);

                System.out.println("Pedestrian list length: "+ pedestrianList.size());
                pedestrianList.forEach(p ->System.out.println("Position: " + p.getX() + ", " + p.getY()));
                objectOutputStream.writeObject(pedestrianList);
                pedestrianList.clear();
                //System.out.println("End of loop");
            }
            System.out.println("Socket is closed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                objectInputStream.close();
                objectOutputStream.close();
                System.out.println("Connection to server has been terminated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        new SimulationClient();
    }
}
