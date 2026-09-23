package com.na.rutaexpress.shipments.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.na.rutaexpress.shipments.dto.ServiceShippingDTO;

@Component
public class CatalogClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${rutaexpress.services.catalog-url}")
    private String catalogUrl;

    public String obtenerVehicleTypeRequired(String serviceId) {
        ServiceShippingDTO servicio = restTemplate.getForObject(
            catalogUrl + "/api/catalog/services/" + serviceId,
            ServiceShippingDTO.class
        );
        return servicio.getVehicleTypeRequired();
    }

    public void descontarCapacidad(String vehicleType) {
        restTemplate.put(catalogUrl + "/api/catalog/fleet-capacity/" + vehicleType + "/descontar", null);
    }

    public void reponerCapacidad(String vehicleType) {
        restTemplate.put(catalogUrl + "/api/catalog/fleet-capacity/" + vehicleType + "/reponer", null);
    }
}