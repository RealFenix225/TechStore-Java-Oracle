package com.techstore.dao;

import com.techstore.model.Product;
import com.techstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Producto.
 * Centraliza todas las operaciones CRUD y consultas SQL contra la tabla PRODUCTOS
 *
 * @author  César Galvez
 * @version 1.0
 */

public class ProductDAO {

    /**
     * Obtiene el stock actual de un producto específico consultando la BBDD.
     *
     * @param productId             ID del producto a consultar.
     * @return                      El stock actual (entero) o -1 si el producto no existe.
     * @throws SQLException         Si ocurre un error de conexión o consulta.
     */
    public int getCurrentStock(int productId) throws SQLException {
        String sql = "SELECT stock_actual FROM PRODUCTOS WHERE producto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("STOCK_ACTUAL");
                } else {
                    return -1; // Producto no existe
                }
            }
        }
    }

    /**
     * Registra un nuevo producto en la base de datos.
     * Gestiona la conversión de tipos (BigDecimal, Boolean) y nulos.
     *
     * @param product               Objeto Product con los datos a insertar.
     * @throws SQLException         Si falla la inserción (ej: SKU duplicado).
     */

    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO PRODUCTOS (sku, nombre, descripcion, precio_venta, precio_costo, stock_actual, categoria_id, proveedor_id, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, product.getSku());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());      // precio_venta
            pstmt.setBigDecimal(5, product.getCostPrice());  // precio_costo
            pstmt.setInt(6, product.getStock());
            pstmt.setInt(7, product.getCategoryId());

            if (product.getProviderId() != null) {
                pstmt.setInt(8, product.getProviderId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            pstmt.setInt(9, product.isActive() ? 1 : 0);

            pstmt.executeUpdate();
            System.out.println("✅ Producto registrado: " + product.getSku() + " - " + product.getName());
        }
    }

    /**
     * Recupera el catálogo completo de productos ordenados por ID
     *
     * @return                      Lista de objetos Product.
     * @throws SQLException         Si falla la consulta.
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS ORDER BY producto_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapRowToProduct(rs)); // Usamos el método auxiliar para no repetir código
            }
        }
        return products;
    }

    /**
     * Actualiza el stock de un producto (suma o resta).
     * La operación es atómica en base de datos para asegurar consistencia.
     *
     * @param productId                 ID del producto a actualizar.
     * @param quantityChange            Cantidad a sumar (positivo) o restar (negativo)
     * @throws SQLException             Si el producto no existe o falla la actualización
     */
    public void updateStock(int productId, int quantityChange) throws SQLException {
        String sql = "UPDATE PRODUCTOS SET stock_actual = stock_actual + ?, actualizado_en = CURRENT_TIMESTAMP WHERE producto_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, productId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Producto ID " + productId + " no encontrado.");
            }
        }
    }

    /**
     * Busca productos cuyo stock esté por debajo del mínimo indicado.
     *
     * @param minimumStock          Cantidad límite para considerar stock crítico.
     * @return                      Lista de productos en alerta.
     * @throws SQLException         Si falla la consulta.
     */
    public List<Product> getProductWithLowStock(int minimumStock) throws SQLException {
        List<Product> lowStockProducts = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTOS WHERE stock_actual < ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, minimumStock);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lowStockProducts.add(mapRowToProduct(rs)); // ¡Reutilizamos la lógica correcta!
                }
            }
        }
        return lowStockProducts;
    }

    /**
     * Busca un producto específico por su ID único.
     *
     * @param id                ID del producto.
     * @return                  Product encontrado o null si no existe.
     * @throws SQLException     Si falla la consulta.
     */
    public Product getProductById(int id) throws SQLException {
        // CORRECCIÓN FINAL: Usamos SELECT * para traer todo y reutilizar mapRowToProduct
        // (Es más seguro traer todo si ya mapeamos todo en el objeto)
        String sql = "SELECT * FROM PRODUCTOS WHERE PRODUCTO_ID = ?";
        Product product = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = mapRowToProduct(rs);
                }
            }
        }
        return product;
    }

    // --- MÉTODOS PRIVADOS (AUXILIARES) ---

    /**
     * Mapea una fila del ResulSet a un objeto Product.
     * Centraliza la lógica de conversión de tipos de la BBDD a Java.
     */
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return Product.builder()
                .id(rs.getInt("PRODUCTO_ID"))
                .sku(rs.getString("SKU"))
                .name(rs.getString("NOMBRE"))
                .description(rs.getString("DESCRIPCION"))
                .price(rs.getBigDecimal("PRECIO_VENTA"))    // Correcto
                .costPrice(rs.getBigDecimal("PRECIO_COSTO")) // Correcto
                .stock(rs.getInt("STOCK_ACTUAL"))
                .categoryId(rs.getInt("CATEGORIA_ID"))
                .providerId(rs.getObject("PROVEEDOR_ID", Integer.class)) // Manejo seguro de nulos
                .active(rs.getInt("ACTIVO") == 1) // Conversión correcta Number(1) -> Boolean
                .build();
    }
}