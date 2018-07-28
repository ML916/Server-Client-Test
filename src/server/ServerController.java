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

    public void initModel(Server server){
        this.server = server;
        /*server.simulationHandler.addListener(new CorridorListener() {
            @Override
            public void onCorridorChange() {
                updateCorridorCanvas();
            }

            @Override
            public void onRemovedPedestrians() {

            }
        });*/
        server.simulationHandler.addListener(() -> updateCorridorCanvas());
        this.server.start();
        goalRectangleSetup();

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
            server.getCorridor().getPedestrianList().forEach(p ->
                    circles.add(p.circle));
            progressIndicator.setProgress(server.getCorridor().progressReport());
            progressBar.setProgress(server.getCorridor().progressReport());
            server.getCorridor().removePedestriansInGoalArea();
        });
    }

    public void onStartButtonAction(ActionEvent actionEvent) {
        System.out.println("Start button pressed");
        if(!server.simulationHandler.isSimulationActive())
            server.simulationHandler.toggleIsConnectionActive();
        server.simulationHandler.start();
    }

    public void onPauseButtonAction(ActionEvent actionEvent) {
        if(server.simulationHandler.isSimulationActive()){
            server.simulationHandler.toggleIsConnectionActive();
        }
    }

    public void onStopButtonAction(ActionEvent actionEvent) {
        server.toggleIsServerOn();
        server.simulationHandler.toggleIsConnectionActive();
    }
}
