package com.techstore.dao;

import com.techstore.model.StockMovement;
import com.techstore.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de Acceso a Datos (DAO) para el historial de movimientos.
 * Registra entradas y salidas de stock y genera reportes de actividad.
 *
 * @author César Gálvez
 * @version 1.0
 */
public class StockMovementDAO {

    /**
     * Registra un nuevo movimiento (Venta o Compra) en la base de datos.
     * La fecha se asigna automáticamente por defecto en Oracle.
     *
     * @param movement Objeto StockMovement con los detalles.
     * @throws SQLException Si ocurre un error al guardar.
     */
    public void insert(StockMovement movement) throws SQLException {
        String sql = "INSERT INTO MOVIMIENTOS_STOCK (producto_id, tipo, cantidad, notas) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movement.getProductId());
            pstmt.setString(2, movement.getType());
            pstmt.setInt(3, movement.getQuantity());
            pstmt.setString(4, movement.getNotes());

            pstmt.executeUpdate();
            // Silencio operativo: El servicio principal confirmará el éxito.
        }
    }

    /**
     * Obtiene los últimos movimientos registrados.
     * Utiliza sintaxis optimizada de Oracle 12c (FETCH FIRST).
     *
     * @param limit Número máximo de movimientos a recuperar.
     * @return Lista de movimientos ordenados del más reciente al más antiguo.
     * @throws SQLException Si falla la consulta.
     */
    public List<StockMovement> getRecentMovements(int limit) throws SQLException {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM MOVIMIENTOS_STOCK ORDER BY movimiento_id DESC FETCH FIRST ? ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp fecha = rs.getTimestamp("fecha_mov");

                    StockMovement mov = StockMovement.builder()
                            .id(rs.getInt("movimiento_id"))
                            .productId(rs.getInt("producto_id"))
                            .type(rs.getString("tipo"))
                            .quantity(rs.getInt("cantidad"))
                            .date(fecha != null ? fecha.toLocalDateTime() : null) // Protección contra nulos
                            .notes(rs.getString("notas"))
                            .build();
                    movements.add(mov);
                }
            }
        }
        return movements;
    }

    /**
     * Genera un ranking de los productos más vendidos.
     * Realiza una agregación (SUM) y un JOIN con la tabla de productos.
     *
     * @return Mapa ordenado (Top 1, Top 2...) con NombreProducto -> CantidadVendida.
     * @throws SQLException Si falla el reporte.
     */
    public Map<String, Integer> getBestSellers() throws SQLException {
        // Uso LinkedHashMap para mantener el orden de inserción (El 1º sigue siendo el 1º).
        Map<String, Integer> ranking = new LinkedHashMap<>();

        // Este Query es una belleza. Agrupa por nombre y suma cantidades, solo tipo 'VENTA'
        String sql = "SELECT p.NOMBRE, SUM(m.CANTIDAD) as TOTAL_VENDIDO " +
                "FROM MOVIMIENTOS_STOCK m " +
                "JOIN PRODUCTOS p ON m.PRODUCTO_ID = p.PRODUCTO_ID " +
                "WHERE m.TIPO = 'VENTA' " +
                "GROUP BY p.NOMBRE " +
                "ORDER BY TOTAL_VENDIDO DESC " +
                "FETCH FIRST 3 ROWS ONLY";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nombreProducto = rs.getString("NOMBRE");
                int totalVendido = rs.getInt("TOTAL_VENDIDO");

                ranking.put(nombreProducto, totalVendido);
            }
        }
        return ranking;
    }
}