package com.grupoDistribuidos.Model.Entidades;

public class Producto {
    private int idProducto;
    private int cantiProducto;
    private String nombreProducto;

    public Producto(int idProducto, int cantiProducto, String nombreProducto) {
        this.idProducto = idProducto;
        this.cantiProducto = cantiProducto;
        this.nombreProducto = nombreProducto;
    }
    public Producto(){
        
    }

    public int getIdProducto() {
        return this.idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantiProducto() {
        return this.cantiProducto;
    }

    public String toString() {
        String str = "ID Producto: " + idProducto;
        str = str + " Nombre: " + nombreProducto;
        str = str + " CantiProducto: "+cantiProducto+".";

        return str;

    }

    public void setCantiProducto(int cantiProducto) {
        this.cantiProducto = cantiProducto;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

}
