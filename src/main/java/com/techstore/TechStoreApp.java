package com.techstore;

import com.techstore.dao.ProductDAO;
import com.techstore.dao.StockMovementDAO;
import com.techstore.model.Product;
import com.techstore.model.StockMovement;
import com.techstore.service.InventoryService;
import com.techstore.service.ReportService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Clase principal de la aplicación TechStore (Interfaz de Consola).
 * Gestiona el menú principal y la interacción con el usuario.
 *
 * @author César Gálvez
 * @version 1.0
 */

public class TechStoreApp {

    // --- VARIABLES GLOBALES (STATIC) ---
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductDAO productDAO = new ProductDAO();
    private static final InventoryService inventoryService = new InventoryService();
    private static final ReportService reportService = new ReportService();
    private static final StockMovementDAO stockMovementDAO = new StockMovementDAO();

    public static void main(String[] args) {
        System.out.println("=======================================");
        System.out.println("BIENVENIDO AL TECHSTORE MAIN v1.0");
        System.out.println("=======================================");

        // Bucle Infinito
        while (true) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Ver Catálogo Completo");
            System.out.println("2. Realizar una Venta");
            System.out.println("3. Radar de Stock Bajo");
            System.out.println("4. Historial de Movimientos");
            System.out.println("5. Productos Estrella (Best Sellers)");
            System.out.println("6. Reabastecer Almacén");
            System.out.println("7. Exportar a Excel (CSV)");
            System.out.println("8. SALIR");
            System.out.print("Seleccione una opción: ");

            // Validación de entrada numérica robusta
            if (!scanner.hasNextInt()) {
                System.out.println("Error: ¡Debes escribir un número!");
                scanner.nextLine(); // Limpiar TODA la línea de basura
                continue;
            }
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el Enter residual

            // SWITCH PRINCIPAL
            switch (opcion) {
                case 1: mostrarCatalogo(); break;
                case 2: procesarVenta(); break;
                case 3: mostrarRadarStock(); break;
                case 4: mostrarHistorial(); break;
                case 5: mostrarBestSellers(); break;
                case 6: procesarReabastecimiento(); break;
                case 7: exportarReporte(); break;
                case 8:
                    System.out.println("Cerrando sistema...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida. Intente del 1 al 8.");
            }
        }
    }

    //   MÉTODOS DE LA INTERFAZ

    private static void mostrarCatalogo() {
        System.out.println("\n=== CATÁLOGO DE PRODUCTOS ===");
        try {
            List<Product> productos = productDAO.getAllProducts();
            if (productos.isEmpty()) {
                System.out.println("El catálogo está vacío.");
            } else {
                // Cabecera de la tabla
                System.out.printf("%-5s | %-15s | %-30s | %-10s | %s%n", "ID", "SKU", "NOMBRE", "PRECIO", "STOCK");
                System.out.println("--------------------------------------------------------------------------------");

                for (Product p : productos) {
                    System.out.printf("%-5d | %-15s | %-30s | %-10.2f | %d%n",
                            p.getId(), p.getSku(), p.getName(), p.getPrice(), p.getStock());
                }
            }
        } catch (Exception e) {
            System.out.println("Error al cargar catálogo: " + e.getMessage());
        }
    }

    private static void procesarVenta() {
        System.out.println("\n--- NUEVA VENTA ---");
        try {
            System.out.print("ID del producto: ");
            int idVenta = scanner.nextInt();

            System.out.print("Cantidad: ");
            int cantVenta = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            System.out.print("Cliente / Notas: ");
            String notas = scanner.nextLine();

            inventoryService.sellProduct(idVenta, cantVenta, notas);
            System.out.println("VENTA COMPLETADA EXITOSAMENTE.");

        } catch (java.util.InputMismatchException e) {
            System.out.println("ERROR: Debes ingresar números enteros para ID y Cantidad.");
            scanner.nextLine(); // Limpiar basura
        } catch (com.techstore.exception.StockInsufficientException e) {
            System.out.println("AVISO DE STOCK: " + e.getMessage());
            System.out.println("SUGERENCIA: Revisa el inventario antes de vender.");
        } catch (com.techstore.exception.TechStoreException e) {
            System.out.println("ERROR DEL SISTEMA: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR DESCONOCIDO: " + e.getMessage());
        }
    }

    private static void mostrarRadarStock() {
        System.out.println("\n--- RADAR DE STOCK BAJO ---");
        try {
            System.out.print("Define el límite de stock para alerta: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Error: Debes ingresar un número.");
                scanner.nextLine();
                return;
            }
            int limite = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            List<Product> alertas = productDAO.getProductWithLowStock(limite);
            if (alertas.isEmpty()) {
                System.out.println("TODO EN ORDEN. No hay stock bajo.");
            } else {
                System.out.println("ALERTA: " + alertas.size() + " productos críticos:");
                for (Product p : alertas) {
                    System.out.println("[SKU: " + p.getSku() + "] " + p.getName() + " | Quedan: " + p.getStock());
                }
            }
        } catch (Exception e) {
            System.out.println("Fallo en el radar: " + e.getMessage());
        }
    }

    private static void mostrarHistorial() {
        System.out.println("\n--- ÚLTIMOS MOVIMIENTOS ---");
        try {
            List<StockMovement> movimientos = stockMovementDAO.getRecentMovements(10);
            if (movimientos.isEmpty()) {
                System.out.println("No hay movimientos registrados.");
            } else {
                for (StockMovement m : movimientos) {
                    System.out.printf("%-3d | %-9s | %-4d | %s | %s%n",
                            m.getId(),
                            m.getType(),
                            m.getQuantity(),
                            m.getDate().toString().replace("T", " ").substring(0, 16),
                            (m.getNotes() != null ? m.getNotes() : ""));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al leer historial: " + e.getMessage());
        }
    }

    private static void mostrarBestSellers() {
        System.out.println("\n==== PODIO DE VENTAS ====");
        try {
            Map<String, Integer> ranking = stockMovementDAO.getBestSellers();

            if (ranking.isEmpty()) {
                System.out.println("Aún no hay datos suficientes.");
            } else {
                int puesto = 1;
                for (Map.Entry<String, Integer> entry : ranking.entrySet()) {
                    String medalla = (puesto == 1) ? "🥇" : (puesto == 2) ? "🥈" : "🥉";
                    System.out.println(medalla + " Puesto #" + puesto + ": " + entry.getKey() +
                            " (Vendidos: " + entry.getValue() + ")");
                    puesto++;
                }
            }
        } catch (Exception e) {
            System.out.println("Error en el reporte: " + e.getMessage());
        }
    }

    private static void procesarReabastecimiento() {
        System.out.println("\n--- REABASTECIMIENTO ---");
        try {
            System.out.print("ID del producto a reponer: ");
            int idProd = scanner.nextInt();

            System.out.print("Cantidad a ingresar: ");
            int cant = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            System.out.print("Proveedor / Notas de entrada: ");
            String notas = scanner.nextLine();

            inventoryService.addStock(idProd, cant, notas);
            System.out.println("STOCK ACTUALIZADO CORRECTAMENTE.");
        } catch (java.util.InputMismatchException e) {
            System.out.println("ERROR: Solo se aceptan números para ID y Cantidad.");
            scanner.nextLine();
        } catch (com.techstore.exception.TechStoreException e) {
            System.out.println("NO SE PUDO REABASTECER: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR INESPERADO: " + e.getMessage());
        }
    }

    private static void exportarReporte() {
        System.out.println("\n--- EXPORTAR A EXCEL (CSV) ---");
        System.out.print("Nombre del archivo (Enter para 'inventario.csv'): ");

        String nombre = scanner.nextLine();

        if (nombre.trim().isEmpty()) {
            nombre = "inventario_techstore.csv";
        }
        // Llamada al servicio
        reportService.exportInventoryToCSV(nombre);
    }
}