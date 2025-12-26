package com.techstore.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

public class Provider {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private boolean active;
    private LocalDateTime createdAt;
}
