package org.example;

import javax.xml.transform.Result;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

public class DBHelper {
    private Connection con;
    private Statement s;

    public DBHelper() {
        conexion();
    }

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
            setOcupada(idMesa, false);
        } catch (SQLException e) {
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
            ResultSet rs = s.executeQuery("select * from consumicion where mesa = " + idMesa);
            if (!rs.next()) {
                setOcupada(idMesa, false);
            }
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
