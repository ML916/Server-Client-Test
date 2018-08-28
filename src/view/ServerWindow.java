package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Corridor;
import model.SimulationHandler;
import server.Server;
import controller.ServerController;

import java.io.IOException;

import static model.SimulationHandler.SimulationStatus.*;

/**
 * ServerWindow is the start of the application
 */
public class ServerWindow extends Application {

    /**
     * Loads the GUI from serverwindow.fxml, initiates a SimulationHandler, Server and then sends them all to the ServerController
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serverwindow.fxml"));
        Parent root = fxmlLoader.load();

        ServerController controller = fxmlLoader.getController();
        SimulationHandler simulationHandler = new SimulationHandler(
                new Corridor(240,400, 180));
        Server server = new Server(simulationHandler);
        controller.initModel(server);

        primaryStage.setOnCloseRequest(e -> {
            server.simulationHandler.setSimulationStatus(OFF);
            server.setIsServerOn(false);
            try {
                server.getServerSocket().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            server.interrupt();
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
