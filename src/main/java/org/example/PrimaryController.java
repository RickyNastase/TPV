package org.example;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Clase que actuará como programa principal y se encargará de la mayoría de procesos. Está conectado en "primary.fxml".
 */
public class PrimaryController {
    // Se establecen y recogen las variables necesarias.
    @FXML
    private Label fecha, cantidad, total;
    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private GridPane grid;
    @FXML
    private ImageView cafes, vinos, refrescos, alcohol, cervezas, desayunos, bocadillos, montaditos, raciones, postres, cobrar, sala, facturar;
    private DBHelper db;
    private String fechaActual;
    private static int idMesa;

    /**
     * Método que redirige a "sala.fxml" donde se encuentra la interfaz de las mesas.
     */
    @FXML
    private void verSala() {
        try {
            App.setRoot("sala");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que se ejecuta al inicializar la clase.
     * Se recoge la clase que actúa como intermediaria con la base de datos, el id de la mesa sobre la cual se van a ejecutar los cambios y
     * se llama a los métodos que queremos que se ejecuten siempre al cargarse esta clase.
     * Se setea la posición del grid y los elementos que contendrá la tabla de los productos.
     */
    @FXML
    public void initialize() {
        db = new DBHelper();
        idMesa = App.idMesa;
        setFecha();
        grid.setAlignment(Pos.CENTER);

        TableColumn<Producto, Integer> colCodigo = new TableColumn<>("Código");
        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        TableColumn<Producto, Integer> colCantidad = new TableColumn<>("Cantidad");

        tablaProductos.getColumns().addAll(colCodigo, colNombre, colPrecio, colCantidad);

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        handlers();
        cargarTabla();
    }

    /**
     * Se borra el texto donde se muestra la cantidad de los productos.
     */
    @FXML
    private void borrar() {
        cantidad.setText("");
    }

    /**
     * Se establece el importe total de los productos que se encuentran en la tabla como consumición de la mesa en concreto.
     */
    private void setTotal() {
        total.setText(db.calcularTotal(idMesa) + "€");
    }

    /**
     * Se elimina el producto que esté seleccionado (si lo está) de la tabla.
     */
    @FXML
    private void eliminarProducto() {
        Producto p = tablaProductos.getSelectionModel().getSelectedItem();
        if (p != null) {
            db.eliminarConsumicion(idMesa, p.getCodigo());
            cargarTabla();
        }
    }

    /**
     * Método llamado por los botones dígitos para establecer la cantidad en el texto. Se va añadiendo al texto los números que se recogen
     * de cada botón presionado. En caso de ser "0" el primer botón presionado, no se añade.
     * @param event Evento al presionar los botones.
     */
    @FXML
    private void setCantidad(ActionEvent event) {
        Button boton = (Button) event.getSource();
        if (cantidad.getText().isEmpty() & boton.getText().equals("0")) {
        } else {
            cantidad.setText(cantidad.getText() + boton.getText());
        }
    }

    /**
     * Si hay un producto de la tabla seleccionado y el texto que contiene la cantidad no está vacío, se establece la nueva cantidad del producto
     * seleccionado a la cantidad recogida.
     * Se establece en la base de datos, se limpia el texto y se carga de nuevo la tabla.
     */
    @FXML
    private void aceptar() {
        if (!cantidad.getText().isEmpty()) {
            int cant = Integer.parseInt(cantidad.getText());
            Producto p = tablaProductos.getSelectionModel().getSelectedItem();
            if (p != null) {
                db.aniadirCantidad(cant, idMesa, p.getCodigo());
                cantidad.setText("");
                cargarTabla();
            }
        }
    }

    /**
     * Método encargado de cargar la tabla con los productos de la mesa actual desde la base de datos.
     * Se setea automáticamente también el importe total de esos productos en la interfaz.
     */
    private void cargarTabla() {
        tablaProductos.getItems().clear();
        tablaProductos.getItems().addAll(db.getProductosMesa(idMesa));
        setTotal();
    }

    /**
     * Si hay una mesa seleccionada, se añade el producto como consumicion a la mesa y se vuelve a cargar la tabla con los datos de la misma.
     * En caso de no haber mesa seleccionada, se muestra el error correspondiente por pantalla.
     * @param nombreProducto Nombre del producto a añadir.
     */
    private void addProducto(String nombreProducto) {
        if (idMesa > 0) {
            db.addProductoMesa(nombreProducto, idMesa);
            cargarTabla();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Error.");
            alert.setContentText("No hay ninguna mesa seleccionada.");
            alert.showAndWait();
        }
    }

    /**
     * Se setea la fecha y hora actuales en la interfaz.
     */
    private void setFecha() {
        Date date = new Date();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fechaActual = formato.format(date);
        fecha.setText("Fecha: " + fechaActual);
    }

    /**
     * Método que setea los EventHandler de los botones.
     */
    private void handlers() {
        /**
         * Botón que redirige a la interfaz de la sala.
         */
        sala.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                verSala();
            }
        });
        /**
         * Botón que, si la tabla no está vacía, cobra la mesa en concreto, añade la factura a la base de datos, genera el comprobante y
         * carga la tabla para mostrarla vaciada de nuevo.
         * En caso de estar vacía la tabla, se muestra el error correspondiente.
         */
        cobrar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (tablaProductos.getItems().size() > 0) {
                    db.generarFactura(idMesa);
                    db.generarComprobante(idMesa);
                    cargarTabla();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setTitle("Error.");
                    alert.setContentText("No hay productos para cobrar.");
                    alert.showAndWait();
                }
            }
        });
        /**
         * Botón encargado de generar la factura diaria del día actual.
         */
        facturar.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                db.generarFacturaDiaria(fechaActual);
            }
        });

        // A partir de aquí se setean los botones para cada imagen de cada categoria de productos. Al pinchar sobre estos, se llama a la función con su
        // categoria específica para mostrar en el grid los productos relacionados con la misma.
        cafes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
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

    /**
     * Método encargado de recoger las imágenes de los productos según la categoría especificada y establecerlos en el grid.
     * Según la categoría se recogen los productos de una carpeta u otra (la carpeta se llama igual que la categoria).
     * A cada imagen se le establece un evento en el cual, si son pinchadas, se añade el producto seleccionado como consumición de la mesa actual.
     * Esto se realiza pasando por parámetro al método encargado de añadirlo, el nombre de la imagen sin el ".png", pues en la base de datos
     * se llama de la misma manera.
     * @param categoria Categoria de los produtos a mostrar.
     */
    public void productosGrid(String categoria) {
        try {
            grid.getChildren().clear();
            File f = new File(getClass().getResource("/imagenes/" + categoria).toURI());
            File[] imagenes = f.listFiles();
            if (imagenes != null) {
                int row = 0;
                int column = 0;
                for (File imagen : imagenes) {
                    ImageView img = new ImageView(new Image(imagen.toURI().toString()));
                    img.setFitWidth(60);
                    img.setPreserveRatio(true);
                    Label nombre = new Label(imagen.getName().split("\\.")[0].toUpperCase());

                    img.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            String[] nombre = imagen.getName().split(".png");
                            String p = nombre[0];
                            addProducto(p);
                        }
                    });

                    VBox vb = new VBox(img, nombre);
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
