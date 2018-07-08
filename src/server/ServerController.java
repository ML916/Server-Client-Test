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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import model.CorridorListener;

public class ServerController {
    public Server server;
    public BorderPane mainBorderPane;
    public Pane canvasPane;
    public Button firstButton;

    private ObservableList<Circle> circles = FXCollections.observableArrayList();

    public void initModel(Server server){
        this.server = server;
        server.communicationHandler.addListener(() -> updateCorridorCanvas());
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
        Rectangle corridorRectangle = new Rectangle(server.getCorridor().getWidth(),server.getCorridor().getHeight(),Color.WHITE);
        corridorRectangle.setX(0);
        corridorRectangle.setX(0);
        corridorRectangle.setStroke(Color.BLACK);
        corridorRectangle.setStrokeType(StrokeType.CENTERED);
        corridorRectangle.setStrokeWidth(1.0);
        canvasPane.getChildren().add(corridorRectangle);

        Rectangle blueRectangle = new Rectangle(
                10,server.getCorridor().getHeight(), Color.BLUEVIOLET);
        blueRectangle.setX(0);
        blueRectangle.setY(0);
        canvasPane.getChildren().add(blueRectangle);

        Rectangle redRectangle = new Rectangle(
                10,server.getCorridor().getHeight(),Color.ORANGERED);
        redRectangle.setX(server.getCorridor().getWidth()-10);
        redRectangle.setY(0);
        canvasPane.getChildren().add(redRectangle);
    }

    private void updateCorridorCanvas(){
        Platform.runLater(() -> {
            circles.clear();
            System.out.println("Update corridor canvas är igång");
            server.getCorridor().getPedestrianList().forEach(p ->
                    circles.add(p.circle));
        });
    }
}
