package com.na.rutaexpress.shipments.service;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.na.rutaexpress.shipments.model.Shipment;
import com.na.rutaexpress.shipments.model.ShipmentStatus;
import com.na.rutaexpress.shipments.model.ShipmentHistoryStatus;
import com.na.rutaexpress.shipments.repository.ShipmentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;

    private static final Map<ShipmentStatus, Set<ShipmentStatus>> TRANSICIONES = new EnumMap<>(ShipmentStatus.class);
    static {
        TRANSICIONES.put(ShipmentStatus.CREADO, Set.of(ShipmentStatus.ACEPTADO, ShipmentStatus.CANCELADO));
        TRANSICIONES.put(ShipmentStatus.ACEPTADO, Set.of(ShipmentStatus.EN_BODEGA, ShipmentStatus.CANCELADO));
        TRANSICIONES.put(ShipmentStatus.EN_BODEGA, Set.of(ShipmentStatus.EN_RUTA, ShipmentStatus.CANCELADO));
        TRANSICIONES.put(ShipmentStatus.EN_RUTA, Set.of(ShipmentStatus.ENTREGADO, ShipmentStatus.CANCELADO));
        TRANSICIONES.put(ShipmentStatus.ENTREGADO, Set.of());
        TRANSICIONES.put(ShipmentStatus.CANCELADO, Set.of());
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }

    public Shipment findById(Long id) {
        return shipmentRepository.findById(id).get();
    }

    public Shipment crear(Shipment nuevo) {
        nuevo.setTrackingCode("RE-" + java.time.Year.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        nuevo.setStatus(ShipmentStatus.CREADO);
        nuevo.setCreatedAt(Instant.now());

        Shipment guardado = shipmentRepository.save(nuevo);

        ShipmentHistoryStatus evento = new ShipmentHistoryStatus(guardado, ShipmentStatus.CREADO, nuevo.getCreatedByUserId());
        guardado.getHistoryStatus().add(evento);

        return shipmentRepository.save(guardado);
    }

    public Shipment cambiarEstado(Long id, ShipmentStatus nuevoStatus, String byUserId) {
        Shipment shipment = shipmentRepository.findById(id).get();
        ShipmentStatus actual = shipment.getStatus();

        if (!TRANSICIONES.get(actual).contains(nuevoStatus)) {
            throw new RuntimeException("No se puede pasar de " + actual + " a " + nuevoStatus);
        }

        shipment.setStatus(nuevoStatus);
        shipment.setUpdatedAt(Instant.now());

        if (nuevoStatus == ShipmentStatus.ACEPTADO) {
            shipment.setAssignedDispatcherId(byUserId);
        }

        ShipmentHistoryStatus evento = new ShipmentHistoryStatus(shipment, nuevoStatus, byUserId);
        shipment.getHistoryStatus().add(evento);

        return shipmentRepository.save(shipment);
    }
}