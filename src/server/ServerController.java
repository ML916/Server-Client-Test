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
import model.Pedestrian;

public class ServerController {
    public Server server;
    public BorderPane mainBorderPane;
    public Pane canvasPane;

    public ProgressIndicator progressIndicator;
    public ProgressBar progressBar;

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
                                "The server is now active and accepting connections from clients"));
            }

            @Override
            public void onServerDisconnected() {
                Platform.runLater(() ->
                        AlertBox.display("Server disconnected",
                                "The server has been disconnected and will no longer accept new connections"));

            }
        });
        server.simulationHandler.addCorridorListener(() -> updateCorridorCanvas());
        server.simulationHandler.addSimulationHandlerListener(new SimulationHandlerListener() {
            @Override
            public void onConnectionDropped() {
                /*Platform.runLater(() ->
                        AlertBox.display("Connection dropped", "Connection with a client has been dropped.\n" +
                        "Simulation will now shift to the remaining clients"));
                startButton.setDisable(false);
                pauseButton.setDisable(true);
                stopButton.setDisable(true);
                if(server.simulationHandler.isSimulationActive()){
                    server.simulationHandler.toggleIsSimulationActive();
                }*/
            }

            @Override
            public void onConnectionAccepted() {
                Platform.runLater(()-> AlertBox.display("New client connected",
                        "A new client has connected to the server.\n"));
                //AlertBox.display("New client connected", "A new client has connected to the server.\n");
                //System.out.println("Connection accepted event");
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
            progressIndicator.setProgress(server.getCorridor().progressReport());
            progressBar.setProgress(server.getCorridor().progressReport());
            //server.getCorridor().removePedestriansInGoalArea();
        });
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
        server.simulationHandler.setIsSimulationActive(true);
        if(!server.simulationHandler.isAlive()){
            server.simulationHandler.start();
        }
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        System.out.println("Pressed start.");
    }

    public void onPauseButtonAction(ActionEvent actionEvent) {
        startButton.setDisable(false);
        server.simulationHandler.setIsSimulationActive(false);
    }

    public void onStopButtonAction(ActionEvent actionEvent) {
        startButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        server.setIsServerOn(false);
        server.simulationHandler.setIsSimulationActive(false);
    }
}
