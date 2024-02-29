package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.List;

public class SalaController {
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
    private void mesaSeleccionada(ActionEvent event){
        Button mesa = (Button) event.getSource();
        App.idMesa = Integer.parseInt(mesa.getText());
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
