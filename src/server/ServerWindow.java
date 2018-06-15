package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverwindow.fxml"));
        Parent root = fxmlLoader.load();

        ServerController controller = fxmlLoader.getController();
        Server server = new Server();
        controller.initModel(server);

        //primaryStage.setOnCloseRequest(event -> System.out.println("Closing window"));
        //primaryStage.setOnCloseRequest(event -> server.toggleIsServerOn());

        primaryStage.setTitle("Server Window");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
