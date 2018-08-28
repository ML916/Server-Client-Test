package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import listener_interfaces.ConnectionListener;
import listener_interfaces.ServerListener;
import model.Corridor;
import model.Pedestrian;
import server.Server;

import static model.SimulationHandler.SimulationStatus.*;

/**
 * Controller that interacts between the Server, SimulationHandler, serverwindow.fxml and ServerWindow classes.
 */
public class ServerController {
    private Server server;
    private Corridor corridor;
    public BorderPane mainBorderPane;
    public Pane canvasPane;

    public Button startButton;
    public Button pauseButton;
    public Button stopButton;
    public ListView messageListView;
    public Label simulationStatusLabel;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();
    private ObservableList<String> messages = FXCollections.observableArrayList("Welcome to the pedestrian simulation program.");

    public ServerController(){
        circles.addListener((ListChangeListener<Circle>) c->  {
            while(c.next()) {
                if(c.wasAdded()) {
                    canvasPane.getChildren().addAll(c.getAddedSubList());
                }
                if(c.wasRemoved()) {
                    canvasPane.getChildren().removeAll(c.getRemoved());
                }
            }
        });
    }

    /**
     * Initiates the graphical interface as well as adding listeners to the simulationHandler and server objects
     * @param server The server object the controller works with
     */
    public void initModel(Server server){
        messageListView.setItems(messages);
        this.server = server;
        this.corridor = server.simulationHandler.getCorridor();
        server.addServerListener(new ServerListener() {
            @Override
            public void onServerIsAlive() {
                Platform.runLater(() -> messages.add(0, "The server is now active and accepting connections from clients"));
            }

            @Override
            public void onServerDisconnected() {
                Platform.runLater(() -> messages.add(0, "The server has been disconnected."));
            }
        });
        server.simulationHandler.addSimulationListener(() -> updateCorridorCanvas());
        server.simulationHandler.addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnectionDropped() {
                setStartButtonStatus();
                System.out.println("Connection dropped");
                if(server.simulationHandler.getNumberOfConnections() >= server.simulationHandler.REQUIRED_NUMBER_OF_CONNECTIONS) {
                    Platform.runLater(() ->
                            messages.add(0,"Connection with a client has been dropped.\n Simulation will now shift to the remaining clients."));
                }
                else{
                    Platform.runLater(() -> {
                        messages.add(0, "The simulation has been paused,\n you may resume it when enough clients have reconnected");
                        simulationStatusLabel.setText("Simulation status: Paused \n" + "Not enough clients connected.");
                        simulationStatusLabel.setTextFill(Color.RED);
                    });
                }
            }

            @Override
            public void onConnectionAccepted() {
                setStartButtonStatus();
                Platform.runLater(()-> {
                    messages.add(0,"A new client has connected to the server.");
                    if(server.simulationHandler.getSimulationStatus() == PAUSED
                            && server.simulationHandler.getNumberOfConnections() >= server.simulationHandler.REQUIRED_NUMBER_OF_CONNECTIONS){
                        simulationStatusLabel.setText("Simulation status: Paused");
                        simulationStatusLabel.setTextFill(Color.ORANGE);
                    }
                });
            }
        });
        this.server.start();
        goalRectangleSetup();
        updateCorridorCanvas();
    }

    /**
     * Sets up the visual representation of the corridor on the GUI
     */
    private void goalRectangleSetup(){
        Rectangle corridorRectangle = new Rectangle(corridor.getWidth(),
                corridor.getHeight(),Color.WHEAT);
        corridorRectangle.setTranslateX(0);
        corridorRectangle.setTranslateY(0);
        corridorRectangle.setStroke(Color.BLACK);
        corridorRectangle.setStrokeType(StrokeType.CENTERED);
        corridorRectangle.setStrokeWidth(1.0);
        canvasPane.getChildren().add(corridorRectangle);

        Rectangle blueRectangle = new Rectangle(
                10,corridor.getHeight(), Color.BLUEVIOLET);
        blueRectangle.setTranslateX(corridor.getWidth()-10);
        blueRectangle.setTranslateY(0);
        canvasPane.getChildren().add(blueRectangle);

        Rectangle redRectangle = new Rectangle(
                10,corridor.getHeight(),Color.ORANGERED);
        redRectangle.setTranslateX(0);
        redRectangle.setTranslateY(0);
        canvasPane.getChildren().add(redRectangle);
    }

    /**
     * Updates the GUI with current positions of all Pedestrian objects within the corridor
     * @see Pedestrian
     * @see Corridor
     */
    private void updateCorridorCanvas(){
        Platform.runLater(() -> {
            circles.clear();
            System.out.println("Updated corridor canvas.");
            synchronized (corridor) {
                corridor.getPedestrianList().forEach(p ->
                        circles.add(setupCircle(p)));
            }
        });
    }

    /**
     * Sets start button status according to how many clients are connected to the server as well as simulation status
     * @see model.SimulationHandler
     * @see model.SimulationHandler.SimulationStatus
     */
    private void setStartButtonStatus(){
        if (server.simulationHandler.getNumberOfConnections() >= server.simulationHandler.REQUIRED_NUMBER_OF_CONNECTIONS
                && server.simulationHandler.getSimulationStatus() != ACTIVE){
            startButton.setDisable(false);
            pauseButton.setDisable(true);
        }
        else {
            startButton.setDisable(true);
        }
    }

    /**
     * Sets up a circle representing a Pedestrian object
     * @param pedestrian The pedestrian object to receive a visual representation
     * @return A circle representing the pedestrian parameter
     */
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

    /**
     * Function called when start button is pressed. Starts the simulation if was paused, resets and starts the simulation if it was turned off.
     * @param actionEvent
     */
    public void onStartButtonAction(ActionEvent actionEvent) {
        if(server.simulationHandler.getSimulationStatus() == OFF) {
            server.simulationHandler.resetSimulation();
            messages.add(0,"Simulation has been restarted.");
        }
        else {
            messages.add(0, "Simulation has been started.");
        }
        server.simulationHandler.setSimulationStatus(ACTIVE);
        if(!server.simulationHandler.isAlive()){
            server.simulationHandler.start();
        }
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        simulationStatusLabel.setText("Simulation status: Active");
        simulationStatusLabel.setTextFill(Color.GREEN);
    }

    /**
     * Called when the pause button is pressed. Pauses the simulation
     * @param actionEvent
     */
    public void onPauseButtonAction(ActionEvent actionEvent) {
        server.simulationHandler.setSimulationStatus(PAUSED);
        setStartButtonStatus();
        simulationStatusLabel.setText("Simulation status: Paused");
        simulationStatusLabel.setTextFill(Color.ORANGE);
        messages.add(0, "Simulation has been paused.");
    }

    /**
     * Called when stop button is pressed. Stops the simulation so that a new simulation is started when the start button is pressed next.
     * @param actionEvent
     */
    public void onStopButtonAction(ActionEvent actionEvent) {
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        server.simulationHandler.setSimulationStatus(OFF);
        setStartButtonStatus();
        messages.add(0, "The simulation has been terminated. \n" + "You may start a new simulation.");
        simulationStatusLabel.setText("Simulation status: Stopped");
        simulationStatusLabel.setTextFill(Color.RED);

    }
}
