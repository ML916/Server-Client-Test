package server;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {
    public static boolean display(String title, String message, boolean isApplicationModal) {
        Stage window = new Stage();

        if(isApplicationModal)
            window.initModality(Modality.APPLICATION_MODAL);
        else
            window.initModality(Modality.WINDOW_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button okButton = new Button("Ok");
        okButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, okButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return true;
    }
}
