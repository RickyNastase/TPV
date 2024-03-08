package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SalaController {
    DBHelper db;
    @FXML
    private Button mesa1, mesa2, mesa3, mesa4, mesa5, mesa6, mesa7, mesa8, mesa9, mesa10, mesa11;
    private List<Button> mesas;

    @FXML
    private void initialize() {
        db = new DBHelper();
        mesas = new ArrayList<>();
        mesas.add(mesa1);
        mesas.add(mesa2);
        mesas.add(mesa3);
        mesas.add(mesa4);
        mesas.add(mesa5);
        mesas.add(mesa6);
        mesas.add(mesa7);
        mesas.add(mesa8);
        mesas.add(mesa9);
        mesas.add(mesa10);
        mesas.add(mesa11);
        setOcupada();
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
    private void mesaSeleccionada(ActionEvent event) {
        Button mesa = (Button) event.getSource();
        App.idMesa = Integer.parseInt(mesa.getText());
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setOcupada() {
        for (Button m : mesas) {
            String id = m.getId().split("a")[1];
            if (db.getOcupada(Integer.parseInt(id))) {
                m.setStyle("-fx-background-color: #FF0000;");
            }
        }
    }

}
