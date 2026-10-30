package com.na.rutaexpress.shipments.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.na.rutaexpress.shipments.dto.ShipmentEventDTO;
import com.na.rutaexpress.shipments.model.Shipment;

import lombok.extern.slf4j.Slf4j;

/**
 * Avisa al microservicio notify que un envio cambio de estado.
 * IMPORTANTE: nunca lanza excepciones. Si notify o RabbitMQ estan caidos,
 * el cambio de estado del envio igual se guarda y la API responde normal.
 */
@Slf4j
@Component
public class NotifyClient {

    private final RestTemplate restTemplate;
    private final String notifyUrl;
    private final CatalogClient catalogClient;

    public NotifyClient(@Value("${rutaexpress.services.notify-url}") String notifyUrl,
                        CatalogClient catalogClient) {
        // RestTemplate propio con timeouts cortos para no dejar colgada la peticion si notify no responde
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.restTemplate = new RestTemplate(factory);
        this.notifyUrl = notifyUrl;
        this.catalogClient = catalogClient;
    }

    public void enviarEvento(Shipment shipment) {
        try {
            ShipmentEventDTO event = new ShipmentEventDTO();
            event.setShipmentId(shipment.getId());
            event.setTrackingCode(shipment.getTrackingCode());
            event.setNewStatus(shipment.getStatus().name());
            event.setWeightKg(shipment.getWeightKg());
            event.setServiceName(nombreServicio(shipment.getServiceId()));
            if (shipment.getRecipient() != null) {
                event.setRecipientName(shipment.getRecipient().getName());
                event.setRecipientEmail(shipment.getRecipient().getEmail());
                event.setDeliveryAddress(shipment.getRecipient().getAddress());
                event.setDeliveryCity(shipment.getRecipient().getCity());
            }

            // Content-Type explicito: sin esto el receptor puede recibir text/plain y rechazar el JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(notifyUrl + "/api/notify/events",
                    new HttpEntity<>(event, headers), String.class);

            log.info("[NOTIFY] Evento enviado tracking={} estado={}",
                    shipment.getTrackingCode(), shipment.getStatus());
        } catch (Exception e) {
            log.warn("[NOTIFY] No se pudo avisar a notify (el cambio de estado igual se guardo): {}",
                    e.getMessage());
        }
    }

    // Si catalog no responde, se usa un nombre generico
    private String nombreServicio(String serviceId) {
        try {
            String nombre = catalogClient.obtenerNombreServicio(serviceId);
            return nombre != null ? nombre : "Servicio " + serviceId;
        } catch (Exception e) {
            log.warn("[NOTIFY] No se pudo obtener el nombre del servicio {}: {}", serviceId, e.getMessage());
            return "Servicio " + serviceId;
        }
    }
}
