package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.SimulationHandler.SimulationStatus;

import static javafx.application.Platform.*;
import static server.SimulationHandler.SimulationStatus.*;

public class ServerWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverwindow.fxml"));
        Parent root = fxmlLoader.load();

        ServerController controller = fxmlLoader.getController();
        Server server = new Server();
        controller.initModel(server);

        primaryStage.setOnCloseRequest(e -> {
            server.simulationHandler.setSimulationStatus(OFF);
            server.setIsServerOn(false);
            Platform.exit();
        });

        primaryStage.setTitle("Server Window");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
