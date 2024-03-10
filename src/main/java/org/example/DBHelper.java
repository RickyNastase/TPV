package org.example;

import java.sql.*;
import java.util.*;

/**
 * Clase que se encarga de toda la interacción entre la base de datos y las interfaces del programa. Contiene los métodos CRUD necesarios.
 */
public class DBHelper {
    // Se definen las variables de conexión.
    private Connection con;
    private Statement s;

    /**
     * Constructor de la clase.
     */
    public DBHelper() {
        conexion();
    }

    /**
     * Método para conectar con la base de datos local MySQL.
     */
    private void conexion() {
        try {
            String db = "basedatos";
            String host = "localhost";
            String port = "3306";
            String urlConnection = "jdbc:mysql://" + host + ":" + port + "/" + db;
            String user = "root";
            String pwd = "ricky_sql23";
            con = DriverManager.getConnection(urlConnection, user, pwd);
            s = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que genera un JasperReport de la factura del día recibiendo por parámetro la fecha ctual y recogiendo los datos
     * de la base de datos, pasándolos como parámetro al reporte a través de un map.
     * @param fecha Fecha del día actual.
     */
    public void generarFacturaDiaria(String fecha) {
        try {
            String fechaActual = fecha.split(" ")[0];
            ResultSet rs = s.executeQuery("select sum(importe) from factura where date(fecha) = '" + fechaActual + "'");
            if (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("fecha", fechaActual);
                map.put("total", rs.getDouble(1));
                new GenerarReporte().generarFacturaDiaria(map, con, fechaActual);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que genera un JasperReport de comprobante recibiendo por parámetro la mesa a la que se le ha cobrado
     * y recogiendo los datos de la base de datos, pasándolos así por parámetro al reporte a través de un map.
     * @param idMesa
     */
    public void generarComprobante(int idMesa) {
        try {
            ResultSet datosComprobante = s.executeQuery("select fac.id,fac.fecha,fac.importe from factura fac inner join mesas m on m.id = fac.mesa where fac.fecha = (select fecha from factura where mesa = " + idMesa + " order by fecha desc limit 1);");

            if (datosComprobante.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("idMesa", String.valueOf(idMesa));
                map.put("idFactura", datosComprobante.getInt(1));
                map.put("fecha", datosComprobante.getTimestamp(2));
                map.put("total", datosComprobante.getDouble(3));
                new GenerarReporte().generarComprobante(map, con, datosComprobante.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que añade a la base de datos la nueva factura generada al cobrar una mesa.
     * Al final, llama a otro método interno más para borrar la mesa de la tabla de consumicion pues
     * ya ha sido cobrada y que se establezca como disponible.
     * @param idMesa Mesa que ha sido cobrada.
     */
    public void generarFactura(int idMesa) {
        try {
            double importe = calcularTotal(idMesa);
            Timestamp fecha = new Timestamp(System.currentTimeMillis());

            PreparedStatement psFactura = con.prepareStatement("insert into factura (mesa,fecha,importe) values (?,?,?)");
            con.setAutoCommit(false);
            psFactura.setInt(1, idMesa);
            psFactura.setTimestamp(2, fecha);
            psFactura.setDouble(3, importe);
            psFactura.executeUpdate();
            con.commit();

            ResultSet rs = s.executeQuery("select id from factura where mesa = " + idMesa + " order by fecha desc limit 1");
            if (rs.next()) {
                int id = rs.getInt(1);
                ResultSet rs2 = s.executeQuery("select * from consumicion where mesa = " + idMesa);
                while (rs2.next()) {
                    PreparedStatement ps = con.prepareStatement("insert into detalles_factura (factura,producto,cantidad) values (?,?,?)");
                    ps.setInt(1, id);
                    ps.setInt(2, rs2.getInt(3));
                    ps.setDouble(3, rs2.getInt(4));
                    ps.executeUpdate();
                    con.commit();
                }
            }
            con.setAutoCommit(true);
            borrarMesa(idMesa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que calcula el importe total actual que tiene la mesa pasada por parámetro sumando los precios de los productos de la base de datos
     * que posee la mesa por la cantidad de cada uno.
     * @param idMesa Mesa para calcular los productos que está consumiendo.
     * @return double Devuelve el precio total de los productos de la mesa.
     */
    public double calcularTotal(int idMesa) {
        double total = 0;
        try {
            ResultSet rs1 = s.executeQuery("select producto,cantidad from consumicion where mesa = " + idMesa);
            if (rs1.next()) {
                do {
                    int idProducto = rs1.getInt(1);
                    int cantidad = rs1.getInt(2);
                    Statement s2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs2 = s2.executeQuery("select precio from productos where codigo = " + idProducto);
                    if (rs2.next()) {
                        double precio = rs2.getDouble(1);
                        total += cantidad * precio;
                    }
                } while (rs1.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    /**
     * Se elimina de la tabla consumicion de la base de datos un producto asociado a una mesa.
     * Luego se comprueba que, si no tiene consumiciones la mesa, se establezca como disponible.
     * @param idMesa Mesa en la que se encuentra el producto.
     * @param producto El producto en concreto para eliminar.
     */
    public void eliminarConsumicion(int idMesa, int producto) {
        try {
            s.execute("delete from consumicion where mesa = " + idMesa + " and producto = " + producto);
            ResultSet rs = s.executeQuery("select * from consumicion where mesa = " + idMesa);
            if (!rs.next()) {
                setOcupada(idMesa, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se añade en la tabla consumicion la nueva cantidad del producto de la mesa especificados.
     * @param cantidad Nueva cantidad del producto.
     * @param idMesa Mesa en la que está el producto.
     * @param producto El producto a modificar.
     */
    public void aniadirCantidad(int cantidad, int idMesa, int producto) {
        try {
            s.execute("update consumicion set cantidad = " + cantidad + " where mesa = " + idMesa + " and producto = " + producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Borrar la mesa especificada de la tabla de consumicion y establecerla como disponible por ende.
     * @param idMesa Mesa a borrar.
     */
    public void borrarMesa(int idMesa) {
        try {
            s.execute("delete from consumicion where mesa = " + idMesa);
            setOcupada(idMesa, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve el estado de disponibilidad de la mesa.
     * @param idMesa Mesa en concreto.
     * @return boolean Si está ocupada devuelve true, si está libre devuelve false.
     */
    public boolean getOcupada(int idMesa) {
        try {
            ResultSet rs = s.executeQuery("select ocupada from mesas where id = " + idMesa);
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Establece la mesa especificada como ocupada o libre.
     * @param idMesa Mesa en concreto.
     * @param ocupada Booleano. Si es true se establece a ocupada, si es false se establece como libre.
     */
    public void setOcupada(int idMesa, boolean ocupada) {
        try {
            s.execute("update mesas set ocupada = " + ocupada + " where id = " + idMesa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se añade un producto a la consumicion de la mesa.
     * Al tener un producto ya consumiendo, se establece la mesa como ocupada.
     * @param nombre Nombre del producto a añadir.
     * @param mesa Mesa en la que se añade.
     */
    public void addProductoMesa(String nombre, int mesa) {
        try {
            ResultSet rs1 = s.executeQuery("select codigo from productos where nombre like '" + nombre + "'");
            rs1.next();
            int id = rs1.getInt(1);
            ResultSet rs = s.executeQuery("select * from consumicion where producto = " + id + " and mesa = " + mesa);
            if (rs.next()) {
                s.execute("update consumicion set cantidad = cantidad + 1 where id = " + rs.getInt(1));
            } else {
                s.execute("insert into consumicion (mesa,producto,cantidad) values (" + mesa + "," + id + "," + 1 + ")");
            }
            setOcupada(mesa, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Si la mesa contiene productos en la tabla consumicion, se devuelven en una lista.
     * @param idMesa Mesa en concreto.
     * @return List<Producto> Lista de productos que está consumiendo la mesa actualmente.
     */
    public List<Producto> getProductosMesa(int idMesa) {
        List<Producto> productos = new ArrayList<>();
        try {
            ResultSet rs = s.executeQuery("select producto,cantidad from consumicion where mesa = " + idMesa);
            Statement s2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            while (rs.next()) {
                ResultSet rs2 = s2.executeQuery("select * from productos where codigo = " + rs.getInt(1));
                if (rs2.next()) {
                    productos.add(new Producto(rs2.getInt(1), rs2.getString(2), rs2.getDouble(3), rs2.getString(4), rs.getInt(2)));
                }
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }
}
