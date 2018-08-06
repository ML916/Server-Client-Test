package server;

import model.Corridor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private boolean isServerOn = true;
    private ServerSocket serverSocket;
    public SimulationHandler simulationHandler;
    private Corridor corridor;
    private List<ServerListener> serverListeners = new ArrayList<ServerListener>();

    public boolean isServerOn() {
        return isServerOn;
    }

    public void toggleIsServerOn(){
        this.isServerOn = !isServerOn;
        System.out.println("toggle is server on: " + isServerOn);
    }

    public Corridor getCorridor() {
        return corridor;
    }

    public Server(){
        super();
        this.corridor = new Corridor(200,400, 150);
        this.isServerOn = true;
        try {
            serverSocket = new ServerSocket(11111);
            simulationHandler = new SimulationHandler(corridor);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        fireServerIsAliveEvent();
        while(isServerOn){
            System.out.println("Waiting for connection");
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client has connected to the server");
                simulationHandler.addConnection(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server: run should be closed now");
        if(simulationHandler.isSimulationActive())
            simulationHandler.toggleIsSimulationActive();
        try {
            serverSocket.close();
            fireServerDisconnectedEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void main(String[] args){
        new Server();
    }
}
