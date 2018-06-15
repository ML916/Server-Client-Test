package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private boolean isServerOn = true;
    private ServerSocket serverSocket;
    private CommunicationHandler communicationHandler;
    private Corridor corridor;

    public boolean isServerOn() {
        return isServerOn;
    }

    public void toggleIsServerOn(){
        this.isServerOn = !isServerOn;
    }

    public Corridor getCorridor() {
        return corridor;
    }

    public Server(){
        super();
        this.corridor = new Corridor(12,300, 200);
        try {
            serverSocket = new ServerSocket(11111);
            communicationHandler = new CommunicationHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void run(){
        while(isServerOn){
            System.out.println("Waiting for connection");
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client has connected to the server");
                communicationHandler.addConnection(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server: run should be closed now");
        try {
            serverSocket.close();
            communicationHandler.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Server();
    }
}
