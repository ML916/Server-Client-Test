package server;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.CorridorListener;

public class ServerController {
    public Server server;
    public BorderPane mainBorderPane;
    public Pane canvasPane;
    public Button firstButton;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();
    private ObservableList<Pedestrian> pedestrians;

    public void initModel(Server server){
        this.server = server;
        server.communicationHandler.addListener(() -> updateCorridorCanvas());
        this.server.start();
        pedestrians = FXCollections.observableList(this.server.getCorridor().getPedestrianList());

        //this.server.getCorridor().getPedestrianList().add(new Pedestrian(2,2));

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
        server.getCorridor().getPedestrianList().forEach(p ->
                circles.add(p.circle));
    }

    private void updateCorridorCanvas(){
        Platform.runLater(() -> {
            circles.clear();
            server.getCorridor().getPedestrianList().forEach(p ->
                    circles.add(p.circle));
        });

    }
}
