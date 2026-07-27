package com.jahm.alixxpres.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoRequest {
    private Long idVenta;
    private String moneda;
}