package com.techstore.service;

import com.techstore.dao.ProductDAO;
import com.techstore.model.Product;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Servicio encargado de la importación masiva de datos.
 * Lee archivos Excel (.xlsx) y carga los productos en la base de datos.
 *
 * @author César Gálvez
 * @version 1.0
 */
public class ExcelImportService {

    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Lee un archivo Excel y procesa cada fila para insertar productos.
     * Omite la cabecera y maneja errores por fila individualmente.
     *
     * @param rutaArchivo Ruta absoluta o relativa del archivo .xlsx
     */
    public void cargarProductosDesdeExcel(String rutaArchivo) {

        // Try-with-resources para asegurar que el archivo se cierra al terminar
        try (FileInputStream file = new FileInputStream(new File(rutaArchivo));
             Workbook workbook = WorkbookFactory.create(file)) {

            // Leemos la primera hoja (índice 0)
            Sheet sheet = workbook.getSheetAt(0);

            int contador = 0;
            System.out.println("⏳ Iniciando lectura de filas del Excel...");

            // Iteramos sobre las filas
            for (Row row : sheet) {
                // Saltamos la cabecera (Fila 0) o filas vacías
                if (row.getRowNum() == 0 || row == null) continue;

                // Extraemos datos celda por celda
                try {
                    // Verificación de seguridad básica para celdas nulas
                    if (row.getCell(0) == null) continue;

                    String nombre = row.getCell(0).getStringCellValue();
                    String descripcion = row.getCell(1).getStringCellValue();
                    String sku = row.getCell(2).getStringCellValue();

                    // POI devuelve números como double. Convertimos.
                    double precioVenta = row.getCell(3).getNumericCellValue();
                    double precioCosto = row.getCell(4).getNumericCellValue();
                    double stock = row.getCell(5).getNumericCellValue();
                    double catId = row.getCell(6).getNumericCellValue();
                    double provId = row.getCell(7).getNumericCellValue();

                    // Construimos el Producto
                    Product producto = Product.builder()
                            .name(nombre)
                            .description(descripcion)
                            .sku(sku)
                            .price(BigDecimal.valueOf(precioVenta))
                            .costPrice(BigDecimal.valueOf(precioCosto))
                            .stock((int) stock)      // Cast a entero
                            .categoryId((int) catId) // Cast a entero
                            .providerId((int) provId)// Cast a entero
                            .active(true)
                            .build();

                    // Insertamos en Base de Datos de forma segura
                    try {
                        productDAO.insert(producto);
                        contador++;
                        // Feedback visual (punto por cada carga)
                        System.out.print(".");
                    } catch (SQLException e) {
                        System.out.println("\nError al insertar SKU " + sku + ": " + e.getMessage());
                    }

                } catch (Exception e) {
                    System.err.println("\n⚠️ Fila " + row.getRowNum() + " omitida: Formato de celda inválido o incompleto.");
                }
            }
            System.out.println("\nIMPORTACIÓN COMPLETADA. Productos cargados exitosamente: " + contador);

        } catch (IOException e) {
            System.err.println("Error fatal leyendo el archivo Excel: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
    }
}