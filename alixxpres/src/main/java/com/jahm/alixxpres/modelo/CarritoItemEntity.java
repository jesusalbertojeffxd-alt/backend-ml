package com.jahm.alixxpres.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_items")
public class CarritoItemEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id")
    private CarritoEntity carrito;
    
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    public CarritoItemEntity() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CarritoEntity getCarrito() { return carrito; }
    public void setCarrito(CarritoEntity carrito) { this.carrito = carrito; }
    public ProductoEntity getProducto() { return producto; }
    public void setProducto(ProductoEntity producto) { this.producto = producto; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}

