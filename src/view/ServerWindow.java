package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Corridor;
import server.Server;
import controller.ServerController;

import java.io.IOException;

import static model.SimulationHandler.SimulationStatus.*;

public class ServerWindow extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverwindow.fxml"));
        Parent root = fxmlLoader.load();

        ServerController controller = fxmlLoader.getController();
        Corridor corridor = new Corridor(240,400, 180);
        Server server = new Server(corridor);
        controller.initModel(server);

        primaryStage.setOnCloseRequest(e -> {
            server.simulationHandler.setSimulationStatus(OFF);
            server.setIsServerOn(false);
            try {
                server.getServerSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
