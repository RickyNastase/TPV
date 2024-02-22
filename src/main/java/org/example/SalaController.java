package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;

public class SalaController {
    private List<Integer> mesas;
    DBHelper db;
    @FXML
    private void initialize(){
        db = new DBHelper();
    }
    @FXML
    private void atras() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mesa1(){
        try {
            App.idMesa = 1;
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
