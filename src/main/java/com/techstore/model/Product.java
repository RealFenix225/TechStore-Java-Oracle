package com.techstore.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data // Genera Getters, setters, toString, equals y hashCode automágicamente
@AllArgsConstructor //Genera el constructor con todos los argumentos
@NoArgsConstructor // Genera el constructor vacío (necesario para JPA/Hibernate más adelante)
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}