package com.techstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // Genera Getters, setters, toString, equals y hashCode automágicamente
@Builder //Genera el constructor con todos los argumentos
@NoArgsConstructor // Genera el constructor vacío (necesario para JPA/Hibernate más adelante)
@AllArgsConstructor

public class Product {
    private Integer id;                         // producto_id
    private String sku;                         // sku (Código único)
    private String name;                        // nombre
    private String description;                 // descripcion
    private BigDecimal price;                   //precio_Venta
    private BigDecimal costPrice;               // precio_costo (NUEVO: Para calcular margen)
    private Integer stock;                      //stock actual
    private Integer categoryId;                 //categoría_id (FK)
    private Integer providerId;                // proveedor_id (FK)
    private boolean active;                     //activo
    private LocalDateTime createdAt;            //creado_en
}