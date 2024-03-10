package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase relacionada con "sala.fxml" que controla la interfaz de la sala y las mesas que contiene.
 */
public class SalaController {
    // Se recogen en variables los datos a usar.
    DBHelper db;
    @FXML
    private Button mesa1, mesa2, mesa3, mesa4, mesa5, mesa6, mesa7, mesa8, mesa9, mesa10, mesa11;
    private List<Button> mesas;

    /**
     * Método que se ejecuta al inincializarse la clase.
     * Se instancia la clase que hará conexión con le base de datos para poder usar sus métodos, la lista de las mesas,
     * y se llaman a los métodos que queremos que se eejecuten al abrir la interfaz.
     */
    @FXML
    private void initialize() {
        db = new DBHelper();
        mesas = new ArrayList<>();
        setMesas();
        setOcupada();
    }

    /**
     * Añade los botones de cada mesa a la lista interna.
     */
    private void setMesas() {
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
    }

    /**
     * Método que vuelve a la interfaz de "primary.fxml".
     */
    @FXML
    private void atras() {
        try {
            App.setRoot("primary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método establecido en todos los botones de las mesas para qe, al ser seleccionada, se recoja el nombre de la mesa (el cual contiene su id),
     * se establezca en la variable del programa principal y se cambie a "primary.fxml".
     * @param event Evento al presionar el botón de la mesa.
     */
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

    /**
     * Se establece que aquellas mesas de la lista de mesas que aparezcan como ocupadas en la base de datos se les muestre el fondo del botón en rojo.
     */
    private void setOcupada() {
        for (Button m : mesas) {
            String id = m.getId().split("a")[1];
            if (db.getOcupada(Integer.parseInt(id))) {
                m.setStyle("-fx-background-color: #FF0000;");
            }
        }
    }

}
