package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private boolean isServerOn = true;
    private ServerSocket serverSocket;
    private CommunicationHandler communicationHandler;
    public Server(){
        super();
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
    }

    public static void main(String[] args){
        new Server();
    }
}
