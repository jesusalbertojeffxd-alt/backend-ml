package com.jahm.alixxpres.repository;

import com.jahm.alixxpres.modelo.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoEntity, Long> {
    Optional<CarritoEntity> findByUsuarioId(Long usuarioId);
    Optional<CarritoEntity> findByUsuarioUsername(String username);
    boolean existsByUsuarioId(Long usuarioId);
}