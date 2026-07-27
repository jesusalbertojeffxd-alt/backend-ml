package com.jahm.alixxpres.modelo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name ="ventas")
@Data
public class VentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private java.time.LocalDateTime fecha;
    private Double total;
    private String estadoPago;

    //-- realciones---
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "venta",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVentaEntity> detalles = new ArrayList<>();

    public VentaEntity(){}

}
