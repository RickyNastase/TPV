package org.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class PrimaryController {
    @FXML
    private Label fecha;
    @FXML
    private TableView tablaProductos;
    @FXML
    private ImageView sala;
    private List<Producto> productos;
    private DBHelper db;

    @FXML
    private void verSala() {
        try {
            App.setRoot("secondary");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        db = new DBHelper();

        sala.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verSala();
            }
        });

        setFecha();
        productos = db.getProductos("vinos");
        datosTabla(productos);
    }

    private void datosTabla(List<Producto> productos) {
        TableColumn<Producto, Integer> colCodigo = new TableColumn<>("Código");
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");
        TableColumn<Producto, Double> colImporte = new TableColumn<>("Importe");
   
        tablaProductos.getColumns().addAll(colCodigo, colNombre, colPrecio, colCantidad, colImporte);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("importe"));

        tablaProductos.getItems().addAll(productos);
    }

    private void setFecha() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(currentDate);
        fecha.setText("Fecha: " + currentDateTime);
    }

}
