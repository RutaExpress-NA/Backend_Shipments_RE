package com.na.rutaexpress.shipments.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    

    @Column(nullable = false, unique = true)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(nullable = false)
    private String serviceId;

    @Column(nullable = false)
    private String createdByUserId;

    @Column
    private String assignedDispatcherId;

    @Embedded
    private Recipient recipient;

    @Column
    private Double weightKg;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ShipmentHistoryStatus> historyStatus = new ArrayList<>();

    public Shipment(String trackingCode, ShipmentStatus status, String serviceId, String createdByUserId, Recipient recipient, Double weightKg) {
        this.trackingCode = trackingCode;
        this.status = status;
        this.serviceId = serviceId;
        this.createdByUserId = createdByUserId;
        this.recipient = recipient;
        this.weightKg = weightKg;
        this.createdAt = Instant.now();
    }

    
}