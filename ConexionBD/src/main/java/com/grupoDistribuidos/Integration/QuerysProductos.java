package com.grupoDistribuidos.Integration;

import com.grupoDistribuidos.Model.Entidades.Producto;
import com.mysql.cj.QueryBindings;
import com.grupoDistribuidos.Controller.Constantes;

import java.sql.Connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class QuerysProductos {
    private Connection connection;
    private PreparedStatement selectAllProductos;
    private PreparedStatement selectProductoXID;
    private PreparedStatement updateProductoXID;

    public QuerysProductos() {
        try {

            connection = DriverManager.getConnection(Constantes.THINCONN,
                    Constantes.USERNAME,
                    Constantes.PASSWORD);

            // Selecciona todos los conductores
            selectAllProductos = connection.prepareStatement(
                    "SELECT * FROM Producto "
                            + "ORDER BY IDProducto");
            selectProductoXID = connection.prepareStatement(
                    "SELECT * FROM Producto "
                            + "WHERE IDProducto = ?");
            updateProductoXID = connection.prepareStatement(
                    "UPDATE Producto "
                            + "Set CantiProducto = ? where IDProducto = ?");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.exit(1);
        }
    }

    public List<Producto> getAllProductos() {
        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = selectAllProductos.executeQuery()) {
            List<Producto> results = new ArrayList<Producto>();

            while (resultSet.next()) {
                results.add(new Producto(
                        resultSet.getInt("IdProducto"),
                        resultSet.getInt("CantiProducto"),
                        resultSet.getString("NombreProducto")));
            }

            return results;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    public Producto obtenerProducto(int id) {
        try {
            selectProductoXID.setInt(1, id); // set Cedula
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
        Producto prod = new Producto();
        try (ResultSet resultSet = selectProductoXID.executeQuery()) {

            while (resultSet.next()) {
                prod = new Producto(
                        resultSet.getInt("IDProducto"),
                        resultSet.getInt("CantiProducto"),
                        resultSet.getString("NombreProducto"));

            }

            return prod;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public boolean actualizarProducto(int ID, int n_canti) throws SQLException {
        try {
            updateProductoXID.setInt(1, n_canti);
            updateProductoXID.setInt(2, ID);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
        updateProductoXID.executeUpdate();
        return true;
    }
}
