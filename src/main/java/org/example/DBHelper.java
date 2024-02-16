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

    public List<Producto> getProductos(String categoria) {
        List<Producto> productos = new ArrayList<>();
        try {
            ResultSet rs = s.executeQuery("select * from productos where categoria like '" + categoria + "'");
            while (rs.next()) {
                productos.add(new Producto(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getString(4)));
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }
}
