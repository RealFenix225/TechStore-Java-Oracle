package com.techstore.service;

import com.techstore.dao.ProductDAO;
import com.techstore.model.Product;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Servicio encargado de la generación de reportes y exportación de datos.
 * Permite volcar el estado actual del inventario a formatos externos (CSV).
 *
 * @author César Gálvez
 * @version 1.0
 */
public class ReportService {

    private final ProductDAO productDAO;

    public ReportService() {
        this.productDAO = new ProductDAO();
    }

    // Constructor para inyección de dependencias (Testing)
    public ReportService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * Escapa caracteres especiales para mantener la integridad del formato CSV.
     * Si el texto contiene separadores (;), comillas o saltos de línea, lo envuelve en comillas dobles.
     *
     * @param value El texto original.
     * @return El texto formateado y seguro para CSV.
     */
    private String escapeCSV(String value) {
        if (value == null) return "";

        boolean mustQuote = value.contains(";") || value.contains("\"") || value.contains("\n");

        if (mustQuote) {
            // Las comillas dobles se escapan duplicándolas (" -> "")
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }

        return value;
    }

    /**
     * Genera un archivo CSV con el listado completo de productos.
     * Incluye cabeceras y maneja datos nulos o caracteres especiales.
     *
     * @param fileName Nombre o ruta del archivo de salida.
     */
    public void exportInventoryToCSV(String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // 1. Escribir Cabecera
            writer.write("ID;SKU;NOMBRE;DESCRIPCION;PRECIO_VENTA;PRECIO_COSTO;STOCK;CATEGORIA;PROVEEDOR;ACTIVO");
            writer.newLine();

            // 2. Obtener datos
            List<Product> products = productDAO.getAllProducts();

            // 3. Escribir filas
            for (Product p : products) {
                String line = String.join(";",
                        String.valueOf(p.getId()),
                        escapeCSV(p.getSku()),
                        escapeCSV(p.getName()),
                        escapeCSV(p.getDescription()),
                        p.getPrice().toString(),
                        p.getCostPrice().toString(),
                        String.valueOf(p.getStock()),
                        String.valueOf(p.getCategoryId()),
                        p.getProviderId() == null ? "" : p.getProviderId().toString(),
                        p.isActive() ? "1" : "0"
                );

                writer.write(line);
                writer.newLine();
            }

            System.out.println("Inventario exportado correctamente a: " + fileName);

        } catch (IOException e) {
            System.out.println("Error de escritura en disco: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general al exportar: " + e.getMessage());
        }
    }
}