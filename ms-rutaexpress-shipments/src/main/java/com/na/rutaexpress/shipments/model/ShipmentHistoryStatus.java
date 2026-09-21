package com.na.rutaexpress.shipments.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "shipment_history_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentHistoryStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id", nullable = false)
    @JsonBackReference
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(nullable = false)
    private String byUserId;

    @Column(nullable = false)
    private Instant timestamp;

    public ShipmentHistoryStatus(Shipment shipment, ShipmentStatus status, String byUserId) {
        this.shipment = shipment;
        this.status = status;
        this.byUserId = byUserId;
        this.timestamp = Instant.now();
    }
}