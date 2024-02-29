package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private Connection con;
    private Statement s;

    public DBHelper() {
        conexion();
    }

    private void conexion() {
        try {
            String db = "tpv";
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

    public void eliminarConsumicion(int idMesa, int producto) {
        try {
            s.execute("delete from consumicion where mesa = " + idMesa + " and producto = " + producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void aniadirCantidad(int cantidad, int idMesa, int producto) {
        try {
            s.execute("update consumicion set cantidad = " + cantidad + " where mesa = " + idMesa + " and producto = " + producto);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void borrarMesa(int idMesa) {
        try {
            s.execute("delete from consumicion where mesa = " + idMesa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public void setOcupada(int idMesa, boolean ocupada) {
        try {
            s.execute("update mesas set ocupada = " + ocupada + " where id = " + idMesa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
