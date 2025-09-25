package com.sweetmanagement.backend.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sweets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;
}