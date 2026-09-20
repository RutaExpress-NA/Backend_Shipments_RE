package com.na.rutaexpress.shipments.controller;

import com.na.rutaexpress.shipments.model.Shipment;
import com.na.rutaexpress.shipments.model.ShipmentStatus;
import com.na.rutaexpress.shipments.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @GetMapping
    public ResponseEntity<List<Shipment>> listar(@RequestParam(required = false) ShipmentStatus status) {
        List<Shipment> shipments = (status != null)
            ? shipmentService.findByStatus(status)
            : shipmentService.findAll();
        if (shipments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shipments);
    }

    @PostMapping
    public ResponseEntity<Shipment> crear(@RequestBody Shipment nuevo) {
        Shipment creado = shipmentService.crear(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shipment> buscar(@PathVariable Long id) {
        try {
            Shipment shipment = shipmentService.findById(id);
            return ResponseEntity.ok(shipment);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            ShipmentStatus nuevoStatus = ShipmentStatus.valueOf(body.get("nuevoStatus"));
            Shipment actualizado = shipmentService.cambiarEstado(id, nuevoStatus, body.get("byUserId"));
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}