package com.jahm.alixxpres.dto;

import java.util.List;

public class CarritoDTO {
    private Long id;
    private List<CarritoItemDTO> items;
    private Double total;
    private Integer totalItems;
    
    public CarritoDTO() {}
    
    public CarritoDTO(Long id, List<CarritoItemDTO> items, Double total, Integer totalItems) {
        this.id = id;
        this.items = items;
        this.total = total;
        this.totalItems = totalItems;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public List<CarritoItemDTO> getItems() { return items; }
    public void setItems(List<CarritoItemDTO> items) { this.items = items; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
}