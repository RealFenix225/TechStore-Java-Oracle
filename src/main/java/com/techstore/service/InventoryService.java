package com.techstore.service;

import com.techstore.dao.ProductDAO;
import com.techstore.dao.StockMovementDAO;
import com.techstore.model.Product;
import com.techstore.model.StockMovement;
import com.techstore.exception.StockInsufficientException;
import com.techstore.exception.TechStoreException;

import java.sql.SQLException;

/**
 * Servicio encargado de la lógica de negocio del inventario.
 * Gestiona las ventas, el reabastecimiento y el registro de movimientos
 *
 * @author Cesar Galvez
 * @version 1.0
 */

public class InventoryService {

    private final ProductDAO productDAO;
    private final StockMovementDAO movementDAO;

    public InventoryService(){
        this.productDAO = new ProductDAO();
        this.movementDAO = new StockMovementDAO();
    }

    public InventoryService(ProductDAO productDAO, StockMovementDAO movementDAO){
        this.productDAO = productDAO;
        this.movementDAO = movementDAO;
    }

    /**
     * Procesa la venta de un producto, actualiza el stock y registra el movimiento
     *
     * @param productId ID del producto a vender.
     * @param quantity Cantidad de unidades a vender
     * @param reason Nota o nombre del cliente asociado a la venta.
     * @throws StockInsufficientException Si la cantidad solicitada es mayor al stock disponible.
     * @throws TechStoreException         Si el producto no existe o hay un error de base de datos.
     */

    // VENDER (Salida de Stock)
    public void sellProduct(int productId, int quantity, String reason) throws TechStoreException, StockInsufficientException {
        try {
            // 1. Verificar stock actual
            int currentStock = productDAO.getCurrentStock(productId);

            if (currentStock == -1) {
                throw new TechStoreException("El producto con ID " + productId + " no existe en la base de datos.");
            }

            // 2. Verificar disponibilidad
            if (currentStock < quantity) {
                throw new StockInsufficientException("Stock insuficiente. Tienes " + currentStock + ", no puedes vender " + quantity);
            }

            // 3. Restar Stock (Paso negativo para que el SQL sume un negativo = resta)
            productDAO.updateStock(productId, -quantity);

            // 4. Registrar Movimiento (VENTA)
            StockMovement mov = StockMovement.builder()
                    .productId(productId)
                    .type("VENTA")
                    .quantity(quantity)
                    .notes(reason)
                    .build();
            movementDAO.insert(mov);

            System.out.println("VENTA REALIZADA: " + quantity + " unidades.");

        } catch (java.sql.SQLException e) {
            throw new TechStoreException("Error crítico de Base de Datos: " + e.getMessage());
        }
    }

    /**
     * Añade stock a un producto existente (Reabastecimiento).
     *
     * @param productId             ID del producto a reponer.
     * @param quantity              Cantidad a ingresar (debe ser mayor a 0).
     * @param reason                Proveedor o motivo de la entrada
     * @throws TechStoreException   Si la cantidad es negativa, el producto no existe o falla la BBDD.
     */

    // REABASTECER (Entrada de Stock)
    public void addStock(int productId, int quantity, String reason) throws TechStoreException {
        try {
            //Valida que la cantidad sea positiva
            if (quantity <= 0) {
                throw new TechStoreException("La cantidad a añadir debe ser mayor a 0.");
            }
            // Verifica que el producto existe
            int currentStock = productDAO.getCurrentStock(productId);
            if (currentStock == -1) {
                throw new TechStoreException("No se puede reabastecer: EL producto ID " + productId + " no existe en la base de datos.");
            }

            //Procede a actualizar
            productDAO.updateStock(productId, quantity); //NOTA: quantity es positivo aquí

            //4. Registrar movimiento
            StockMovement mov = StockMovement.builder()
                    .productId(productId)
                    .type("COMPRA")
                    .quantity(quantity)
                    .notes(reason)
                    .build();
            movementDAO.insert(mov);
        } catch (SQLException e) {
            throw new TechStoreException("Error al intentar guardar en BBDD: " + e.getMessage());
        }
    }
}