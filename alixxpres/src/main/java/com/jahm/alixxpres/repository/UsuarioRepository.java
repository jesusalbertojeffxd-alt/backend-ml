package com.jahm.alixxpres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jahm.alixxpres.modelo.UsuarioEntity;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}