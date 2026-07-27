package com.jahm.alixxpres.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jahm.alixxpres.modelo.ClienteEntity;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByEmail(String email);
}