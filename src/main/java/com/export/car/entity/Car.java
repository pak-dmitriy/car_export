package com.export.car.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "mileage", nullable = false)
    private int mileage;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @Column(name = "transmission", nullable = false)
    private String transmission;

    @Column(name = "engine_volume", precision = 3, scale = 2)
    private BigDecimal engineVolume;

    @Column(name = "body_type", nullable = false)
    private String bodyType;

    @Column(name = "color")
    private String color;

    @Column(name = "country_of_origin")
    private String countryOfOrigin;

    @Column(name = "export_location")
    private String exportLocation;

    @Column(name = "description", length = 5000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CarStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarImage> images = new ArrayList<>();
}
