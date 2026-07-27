package com.jahm.alixxpres.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "detalles_venta")
@Data
public class DetalleVentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // --- Relaciones de llaves Fk ---------
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "venta_id") // llave foranea de venta
    private VentaEntity venta;

    @ManyToOne
    @JoinColumn(name = "producto_id") // llave foranea de producto
    private ProductoEntity producto;
}