package sample;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class Controller {

    @FXML
    private GridPane gridPane;

    public void setup() {
        System.out.println("GRID PANE: " + gridPane);
    }

}
