package com.na.rutaexpress.shipments.dto;

import lombok.Data;

/**
 * Evento que shipments envia a notify cuando un envio se crea o cambia de estado.
 * Los nombres de los campos coinciden con los de ShipmentEvent en notify.
 * eventId y occurredAt no se envian: los completa notify.
 */
@Data
public class ShipmentEventDTO {
    private Long shipmentId;
    private String trackingCode;
    private String newStatus;
    private String recipientName;
    private String recipientEmail;

    // datos de entrega: los usan los correos, la etiqueta PDF y el ticket de picking
    private String deliveryAddress;
    private String deliveryCity;
    private String serviceName;
    private Double weightKg;
}
