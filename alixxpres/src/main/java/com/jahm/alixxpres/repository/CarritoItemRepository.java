package com.jahm.alixxpres.repository;

import com.jahm.alixxpres.modelo.CarritoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItemEntity, Long> {
    List<CarritoItemEntity> findByCarritoId(Long carritoId);
    void deleteByCarritoId(Long carritoId);
}