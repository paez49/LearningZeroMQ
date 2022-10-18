package com.grupoDistribuidos.Controller;

import com.grupoDistribuidos.Model.Entidades.Producto;
import com.grupoDistribuidos.Integration.QuerysProductos;

import java.sql.SQLException;
import java.util.List;

public class FachadaOCR {
    QuerysProductos querysProducs = new QuerysProductos();
    public FachadaOCR(){
        
    }
    
    public List<Producto> ConsultarProductos(){
        return querysProducs.getAllProductos();
    }
    public Producto ObtenerProductoXID(int n_ID){
        return querysProducs.obtenerProducto(n_ID);
    }
    public boolean actualizarProducto(int ID,int n_canti){
        try {
            return querysProducs.actualizarProducto(ID, n_canti);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }
}