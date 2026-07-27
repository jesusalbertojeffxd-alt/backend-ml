package com.jahm.alixxpres.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jahm.alixxpres.modelo.DetalleVentaEntity;

public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity, Long> {
    
    // Buscar detalles por ID de venta
    List<DetalleVentaEntity> findByVentaId(Long ventaId);
    
    // Buscar detalles por ID de producto
    List<DetalleVentaEntity> findByProductoId(Long productoId);
}