package com.jahm.alixxpres.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jahm.alixxpres.modelo.ProductoEntity;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
    Optional<ProductoEntity> findByNombre(String nombre);
    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
    List<ProductoEntity> findByStockLessThan(Integer stock);
    List<ProductoEntity> findByCategoriaId(Long categoriaId);
    List<ProductoEntity> findByProveedorId(Long proveedorId);
    
    @Query("SELECT p FROM ProductoEntity p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<ProductoEntity> buscarPorTermino(@Param("termino") String termino);
}