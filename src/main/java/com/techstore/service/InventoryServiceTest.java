package com.techstore.service;

import com.techstore.exception.StockInsufficientException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias/integración para InventoryService.
 * Verifica el comportamiento ante situaciones de stock insuficiente.
 */
class InventoryServiceTest {

    @Test
    void intentarVenderSinStockDebeLanzarExcepcion() {
        System.out.println("TEST: Venta con Stock Insuficiente (Esperando Excepción)");

        // 1. PREPARACIÓN
        InventoryService service = new InventoryService();
        int idProducto = 1; // Asegúrate de usar un ID que exista en tu BD
        int cantidadAbsurda = 1000000;

        // 2. EJECUCIÓN Y VERIFICACIÓN
        // assertThrows verifica que el código dentro del lambda () -> {} lance la clase indicada.
        // Si no lanza nada, el test falla. Si lanza otra cosa, el test falla.
        assertThrows(StockInsufficientException.class, () -> {
            service.sellProduct(idProducto, cantidadAbsurda, "Test JUnit - Venta Fallida");
        });

        System.out.println("PRUEBA SUPERADA: El servicio lanzó StockInsufficientException como se esperaba.");
    }

    @Test
    void pruebaInicial() {
        assertEquals(10, 5 * 2);
    }
}