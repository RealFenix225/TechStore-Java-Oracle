package com.techstore.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de utilidad para gestionar la conexión a la base de datos.
 * Implementa el patrón Singleton y lee la configuración desde un archivo externo.
 */
public class DatabaseConnection {

    private static final Properties props = new Properties();

    // Bloque estático: Se ejecuta una sola vez al cargar la clase
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.out.println("ERROR FATAL: No se encuentra el archivo 'database.properties'");
                // Si falla esto, no continua, así que lanzamos una RuntimeException
                throw new RuntimeException("Falta configuración de base de datos");
            }
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al leer configuración de base de datos");
        }
    }

    // Constructor privado
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password")
        );
    }
}