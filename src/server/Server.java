package server;

import listener_interfaces.ServerListener;
import model.Corridor;
import model.SimulationHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static model.SimulationHandler.SimulationStatus.*;

/**
 * Server is a thread that listens for connections from clients.
 */
public class Server extends Thread {
    private boolean isServerOn;
    private ServerSocket serverSocket;
    private List<ServerListener> serverListeners = new ArrayList<ServerListener>();
    public SimulationHandler simulationHandler;


    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean getIsServerOn() {
        return isServerOn;
    }

    public void setIsServerOn(boolean serverOn) {
        isServerOn = serverOn;
    }

    public Server(){
        super();
        this.isServerOn = true;
        try {
            serverSocket = new ServerSocket(11111);
            serverSocket.setSoTimeout(1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Server object and assigns its simulationHandler
     * @param simulationHandler A SimulationHandler object to be assigned to the Server
     * @see SimulationHandler
     */
    public Server(SimulationHandler simulationHandler){
        this();
        this.simulationHandler = simulationHandler;
    }

    /**
     * This function listens to new connections on the serverSocket and adds a Connection object to the simulationHandler
     * @see SimulationHandler
     * @see model.SimulationHandler.Connection
     */
    public void run(){
        fireServerIsAliveEvent();
        while(isServerOn){
            System.out.println("Waiting for new connections");
            try {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                simulationHandler.addConnection(clientSocket);
                System.out.println("A new client has connected to the server");
            } catch (IOException e) {

            }
        }
        try {
            System.out.println("Closing server socket");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server has been shutdown.");
    }

    public void addServerListener(ServerListener serverListener){
        serverListeners.add(serverListener);
    }

    private void fireServerIsAliveEvent() {
        for (ServerListener listener: serverListeners) {
            listener.onServerIsAlive();
        }
    }

    private void fireServerDisconnectedEvent() {
        for (ServerListener listener: serverListeners) {
            listener.onServerDisconnected();
        }
    }

}
