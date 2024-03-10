package org.example;

/**
 * Clase objeto que representa los productos y sus características que se consumen en el establecimiento.
 */
public class Producto {
    private int codigo;
    private String nombre;
    private double precio;
    private String categoria;
    private int cantidad;

    /**
     * Constructor parametrizado.
     * @param codigo Número identificador del producto.
     * @param nombre Nombre del producto.
     * @param precio Precio del producto.
     * @param categoria Categoria a la que pertenece.
     * @param cantidad Cantidad de productos consumidos de este tipo.
     */
    public Producto(int codigo, String nombre, double precio, String categoria,int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
