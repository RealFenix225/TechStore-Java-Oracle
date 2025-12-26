package com.techstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Category {
    private Integer id;               // categoria_id
    private String name;             // nombre
    private String description;     // descripcion
    private boolean active;        // activo ( 1 = true, 0 = false)
}
