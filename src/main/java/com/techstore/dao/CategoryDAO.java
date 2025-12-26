package com.techstore.dao;

import com.techstore.model.Category;
import com.techstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la gestión de Categorías.
 * Se encarga de las operaciones CRUD en la tabla CATEGORIAS de Oracle.
 *
 * @author César Galvez
 * @version 1.0
 */

public class CategoryDAO {

    /**
     * Inserta una nueva categoría en la base de datos
     * Convierte el estado activo (boolean) a numérico (1/0) para Oracle.
     *
     * @param category Objeto Category con los datos a insertar.
     * @throws SQLException Si ocurre un error de conexión o restricción SQL.
     */

    //1. MÉTODO PARA INSERTAR UNA NUEVA CATEGORÍA
    public void insert(Category category) throws SQLException {
        String sql = "INSERT INTO CATEGORIAS (nombre, descripcion, activo) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getDescription());
            //Convertimos el boolean de Java (true/false) al Number(1) de Oracle (1/0)
            pstmt.setInt(3, category.isActive() ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("Categoría insertada con éxito: " + category.getName());
        }
    }

    /**
     * Obtiene el listado completo de categorías ordenadas por ID.
     *
     * @return                  Lista de objetos Category.
     * @throws SQLException     Si falla la consulta SQL.
     */

    //2. MÉTODO PARA LISTAR TODAS LAS CATEGORÍAS
    public List<Category> getAll() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM CATEGORIAS ORDER BY categoria_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category cat = Category.builder()
                        .id(rs.getInt("categoria_id"))
                        .name(rs.getString("nombre"))
                        .description(rs.getString("descripcion"))
                        .active(rs.getInt("activo") == 1) //Convertimos 1 a true
                        .build();
                categories.add(cat);
            }
        }
        return categories;
    }
}