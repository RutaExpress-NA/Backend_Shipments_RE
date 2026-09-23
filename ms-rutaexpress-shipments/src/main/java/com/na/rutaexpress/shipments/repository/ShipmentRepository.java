package com.na.rutaexpress.shipments.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.na.rutaexpress.shipments.model.Shipment;
import com.na.rutaexpress.shipments.model.ShipmentStatus;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingCode(String trackingCode);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByAssignedDispatcherId(String assignedDispatcherId);
}   