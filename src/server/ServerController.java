package server;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ServerController {
    public Server server;
    public BorderPane mainBorderPane;
    public Pane canvasPane;
    public Button firstButton;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();

    public void initModel(Server server){
        this.server = server;

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
                circles.add(new Circle(p.getX(), p.getY(), 3.0, Color.RED)));
    }
}
