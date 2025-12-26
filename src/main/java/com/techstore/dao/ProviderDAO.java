package com.techstore.dao;

import com.techstore.model.Provider;
import com.techstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la gestión de Proveedores.
 * Administra el registro y consulta de las empresas que suministran productos.
 *
 * @author César Gálvez
 * @version 1.0
 */

public class ProviderDAO {

    /**
     * Registra un nuevo proveedor en la base de datos.
     *
     * @param provider          Objeto Provider con los datos de contacto.
     * @throws SQLException     Si ocurre un error al insertar (ej: nombre duplicado)
     */
    public void insert(Provider provider) throws SQLException{
        //SQL simple, que Oracle ponga el ID y la fecha (default).
        String sql = "INSERT INTO PROVEEDORES (nombre, contacto_email, telefono, activo) values (?, ?, ?, ?) ";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, provider.getName());
            pstmt.setString(2, provider.getEmail());
            pstmt.setString(3, provider.getPhone());
            pstmt.setInt(4, provider.isActive() ? 1: 0);

            pstmt.executeUpdate();
            System.out.println("Proveedor registrado: "+ provider.getName());
        }
    }

    /**
     * Obtiene la lista completa de proveedores registrados.
     *
     * @return                  Lista de objetos Provider.
     * @throws SQLException     Si falla la conexión o la consulta.
     */

    public List<Provider> getAll() throws SQLException{
        List<Provider> providers = new ArrayList<>();
        String sql = "SELECT * FROM PROVEEDORES ORDER BY proveedor_id";

        try(Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)){

            while(rs.next()){
                Provider p = Provider.builder()
                        .id(rs.getInt("proveedor_id"))
                        .name(rs.getString("nombre"))
                        .email(rs.getString("contacto_email"))
                        .phone(rs.getString("telefono"))
                        .active(rs.getInt("activo") == 1)
                        //Mapeo de Timestamp SQL a LocalDateTime JAVA
                        .createdAt(rs.getTimestamp("creado_en").toLocalDateTime())
                        .build();
                providers.add(p);
            }
        }
        return providers;
    }
}
