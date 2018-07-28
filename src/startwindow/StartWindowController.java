package startwindow;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.ServerWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StartWindowController {
    public Button okButton;
    public ChoiceBox numberChoiceBox;

    public StartWindowController(){
        initNumberChoiceBox();
    }

    public void initNumberChoiceBox(){
        List numberList = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        //numberChoiceBox.setItems(FXCollections.observableArrayList(numberList));

    }

    @FXML
    public void proceedToNextScreen(){
        //numberChoiceBox.getValue();
        System.out.println("hej");
        Stage stage = (Stage) okButton.getScene().getWindow();

    }
}
