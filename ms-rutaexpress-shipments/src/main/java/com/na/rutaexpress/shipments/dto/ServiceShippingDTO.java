package com.na.rutaexpress.shipments.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceShippingDTO {
    private Long id;
    private String vehicleTypeRequired;
}