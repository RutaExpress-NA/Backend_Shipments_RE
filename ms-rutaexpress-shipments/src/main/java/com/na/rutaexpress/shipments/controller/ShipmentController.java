package com.na.rutaexpress.shipments.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.na.rutaexpress.shipments.model.Shipment;
import com.na.rutaexpress.shipments.model.ShipmentStatus;
import com.na.rutaexpress.shipments.service.ShipmentService;

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