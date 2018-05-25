package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        Pedestrian p = new Pedestrian(1);
        Pedestrian p2 = new Pedestrian(2);
        Pedestrian p3 = new Pedestrian(3);
        Pedestrian p4 = new Pedestrian(4);
        System.out.println("p1: pos: " +  p.getPosition() + " ID: " + p.id);
        System.out.println("p2: pos: " +  p2.getPosition() + " ID: " + p2.id);
        System.out.println("p3: pos: " +  p3.getPosition() + " ID: " + p3.id);
        System.out.println("p4: pos: " +  p4.getPosition() + " ID: " + p4.id);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
