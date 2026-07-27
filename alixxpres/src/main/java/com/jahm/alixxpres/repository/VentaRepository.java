package com.jahm.alixxpres.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jahm.alixxpres.modelo.VentaEntity;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {
    List<VentaEntity> findByClienteEmail(String email);
    List<VentaEntity> findByEstadoPago(String estadoPago);
    List<VentaEntity> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<VentaEntity> findByClienteEmailAndEstadoPago(String email, String estadoPago);
    
    @Query("SELECT SUM(v.total) FROM VentaEntity v WHERE v.cliente.email = :email")
    Double sumTotalByCliente(@Param("email") String email);
}