package org.example;

import java.io.File;
import java.io.FileInputStream;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PrimaryController {
    @FXML
    private Label fecha;
    @FXML
    private TableView tablaProductos;
    @FXML
    private ImageView sala;
    @FXML
    private GridPane grid;
    @FXML
    private ImageView cafes, vinos, refrescos, alcohol, cervezas, desayunos, bocadillos, montaditos, raciones, postres;
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
        setFecha();
        grid.setAlignment(Pos.CENTER);

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

        handlers();
    }

    private void datosTabla(List<Producto> productos) {
        tablaProductos.getItems().clear();
        tablaProductos.getItems().addAll(productos);
    }

    private void setFecha() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = dateFormat.format(currentDate);
        fecha.setText("Fecha: " + currentDateTime);
    }

    private void handlers() {
        sala.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verSala();
            }
        });

        cafes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                datosTabla(db.getProductos("cafes"));
                productosGrid("cafes");
            }
        });
        vinos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("vinos");
            }
        });
        refrescos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("refrescos");
            }
        });
        alcohol.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("alcohol");
            }
        });
        cervezas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("cervezas");
            }
        });
        desayunos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("desayunos");
            }
        });
        bocadillos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("bocadillos");
            }
        });
        montaditos.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("montaditos");
            }
        });
        raciones.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("raciones");
            }
        });
        postres.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                productosGrid("postres");
            }
        });
    }

    public void productosGrid(String categoria) {
        try {
            grid.getChildren().clear();
            File f = new File(getClass().getResource("/imgProductos/" + categoria).toURI());
            File[] imagenes = f.listFiles();
            if (imagenes != null) {
                int row = 0;
                int column = 0;
                for (File imagen : imagenes) {
                    ImageView img = new ImageView(new Image(imagen.toURI().toString()));
                    img.setFitWidth(60);
                    img.setPreserveRatio(true);
                    Label nombre = new Label(imagen.getName());
                    VBox vb = new VBox(img,nombre);
                    vb.setAlignment(Pos.CENTER);
                    grid.add(vb, column, row);
                    if (column == 3) {
                        column = 0;
                        row += 1;
                    } else {
                        column += 1;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
