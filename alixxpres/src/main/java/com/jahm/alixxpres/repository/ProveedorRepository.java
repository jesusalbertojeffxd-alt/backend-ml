package com.jahm.alixxpres.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jahm.alixxpres.modelo.ProveedorEntity;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    
    // Métodos básicos de búsqueda
    Optional<ProveedorEntity> findByEmail(String email);
    Optional<ProveedorEntity> findByNombre(String nombre);
    List<ProveedorEntity> findByNombreContainingIgnoreCase(String nombre);
    Optional<ProveedorEntity> findByTelefono(String telefono);
    
    // Métodos de verificación
    boolean existsByEmail(String email);
    boolean existsByNombre(String nombre);
    
    // Búsqueda avanzada por término
    @Query("SELECT p FROM ProveedorEntity p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :termino, '%')) OR p.telefono LIKE CONCAT('%', :termino, '%')")
    List<ProveedorEntity> buscarPorTermino(@Param("termino") String termino);
}