package com.techstore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StockMovement {
    private Integer id;
    private Integer productId;          //Relación con Product
    private String type;                //'ENTRADA', 'VENTA' etc.
    private Integer quantity;
    private LocalDateTime date;
    private String notes;
}