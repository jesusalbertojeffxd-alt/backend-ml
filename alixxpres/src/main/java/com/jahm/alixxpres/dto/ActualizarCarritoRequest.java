package com.jahm.alixxpres.dto;

public class ActualizarCarritoRequest {
    private Long productoId;
    private Integer cantidad;
    
    public ActualizarCarritoRequest() {}
    
    public ActualizarCarritoRequest(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }
    
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}