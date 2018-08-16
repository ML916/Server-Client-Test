package server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import model.Pedestrian;
import server.SimulationHandler.SimulationStatus;

import static server.SimulationHandler.SimulationStatus.*;

public class ServerController {
    public Server server;
    public BorderPane mainBorderPane;
    public Pane canvasPane;

    public Button startButton;
    public Button pauseButton;
    public Button stopButton;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();

    public ServerController(){
        System.out.println("Controller constructor");

        circles.addListener((ListChangeListener<Circle>) c->  {
            while(c.next()) {
                if(c.wasAdded()) {
                    canvasPane.getChildren().addAll(c.getAddedSubList());
                }
                if(c.wasRemoved()) {
                    canvasPane.getChildren().removeAll(c.getRemoved());
                }
                if(c.wasUpdated()){

                }
            }
        });
    }

    public void initModel(Server server){
        this.server = server;
        this.server.start();
        server.addServerListener(new ServerListener() {
            @Override
            public void onServerIsAlive() {
                Platform.runLater(() ->
                        AlertBox.display("Server is active",
                                "The server is now active and accepting connections from clients", true));
            }

            @Override
            public void onServerDisconnected() {
                Platform.runLater(() ->
                        AlertBox.display("Server disconnected",
                                "The server has been disconnected and will no longer accept new connections", true));

            }
        });
        server.simulationHandler.addSimulationListener(() -> updateCorridorCanvas());
        server.simulationHandler.addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnectionDropped() {
                setStartButtonStatus();
                if(server.simulationHandler.getNumberOfConnections() >= server.simulationHandler.REQUIRED_NUMBER_OF_CONNECTIONS) {
                    Platform.runLater(() ->
                            AlertBox.display("Connection dropped", "Connection with a client has been dropped.\n" +
                                    "Simulation will now shift to the remaining clients.", false));
                }
                else{
                    Platform.runLater(() ->
                            AlertBox.display("Simulation interrupted", "Connection with a client has been dropped.\n" +
                                    "The simulation will be paused, you may resume it when enough clients have reconnected", true));
                }

            }

            @Override
            public void onConnectionAccepted() {
                setStartButtonStatus();
                Platform.runLater(()-> AlertBox.display("New client connected",
                        "A new client has connected to the server.\n", false));
            }
        });
        goalRectangleSetup();
        updateCorridorCanvas();
    }

    private void goalRectangleSetup(){
        Rectangle corridorRectangle = new Rectangle(server.getCorridor().getWidth(),
                server.getCorridor().getHeight(),Color.WHEAT);
        corridorRectangle.setTranslateX(0);
        corridorRectangle.setTranslateY(0);
        corridorRectangle.setStroke(Color.BLACK);
        corridorRectangle.setStrokeType(StrokeType.CENTERED);
        corridorRectangle.setStrokeWidth(1.0);
        canvasPane.getChildren().add(corridorRectangle);

        Rectangle blueRectangle = new Rectangle(
                10,server.getCorridor().getHeight(), Color.BLUEVIOLET);
        blueRectangle.setTranslateX(server.getCorridor().getWidth()-10);
        blueRectangle.setTranslateY(0);
        canvasPane.getChildren().add(blueRectangle);

        Rectangle redRectangle = new Rectangle(
                10,server.getCorridor().getHeight(),Color.ORANGERED);
        redRectangle.setTranslateX(0);
        redRectangle.setTranslateY(0);
        canvasPane.getChildren().add(redRectangle);
    }

    private void updateCorridorCanvas(){
        Platform.runLater(() -> {
            circles.clear();
            synchronized (server.getCorridor()) {
                server.getCorridor().pedestrianList().forEach(p ->
                        circles.add(setupCircle(p)));
            }
            //progressIndicator.setProgress(server.getCorridor().progressReport());
            //progressBar.setProgress(server.getCorridor().progressReport());
        });
    }

    private void setStartButtonStatus(){
        if (server.simulationHandler.getNumberOfConnections() >= server.simulationHandler.REQUIRED_NUMBER_OF_CONNECTIONS
                && server.simulationHandler.getSimulationStatus() == PAUSED){
            startButton.setDisable(false);
        }
        else {
            startButton.setDisable(true);
        }
    }

    private Circle setupCircle(Pedestrian pedestrian){
        if (!pedestrian.hasReachedGoal()) {
            switch (pedestrian.DIRECTION) {
                case RIGHT:
                    return new Circle(pedestrian.getX(), pedestrian.getY(), 2.5, Color.BLUE);
                case LEFT:
                    return new Circle(pedestrian.getX(), pedestrian.getY(), 2.5, Color.RED);
            }
        }
        return null;
    }

    public void onStartButtonAction(ActionEvent actionEvent) {
        server.simulationHandler.setSimulationStatus(ACTIVE);
        if(!server.simulationHandler.isAlive()){
            server.simulationHandler.start();
        }
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
    }

    public void onPauseButtonAction(ActionEvent actionEvent) {
        server.simulationHandler.setSimulationStatus(PAUSED);
        setStartButtonStatus();
    }

    public void onStopButtonAction(ActionEvent actionEvent) {
        startButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        server.setIsServerOn(false);
        server.simulationHandler.setSimulationStatus(OFF);
        Stage stage = (Stage) stopButton.getScene().getWindow();
        if(AlertBox.display("Stop button pressed", "The simulation has been terminated", true))
            stage.close();
    }
}
