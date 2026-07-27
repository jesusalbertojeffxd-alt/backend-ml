package com.jahm.alixxpres.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.ClienteEntity;
import com.jahm.alixxpres.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteServices {
    private final ClienteRepository repository;

    // Obtener todos los clientes
    @Transactional(readOnly = true)
    public List<ClienteEntity> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener Cliente por id
    @Transactional(readOnly = true)
    public ClienteEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    // Obtener Cliente por email
    @Transactional(readOnly = true)
    public ClienteEntity obtenerPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));
    }

    // Guardar Cliente
    @Transactional
    public ClienteEntity guardarCliente(ClienteEntity cliente) {
        // Validar que no exista un cliente con el mismo email
        if (cliente.getEmail() != null && repository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con el email: " + cliente.getEmail());
        }
        return repository.save(cliente);
    }

    // Eliminar Cliente
    @Transactional
    public void eliminarCliente(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Actualizar Cliente
    @Transactional
    public ClienteEntity actualizarCliente(Long id, ClienteEntity clienteEntity) {
        ClienteEntity clienteExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no existe con ID: " + id));

        // Si se está actualizando el email, verificar que no esté en uso por otro cliente
        if (clienteEntity.getEmail() != null && 
            !clienteEntity.getEmail().equals(clienteExistente.getEmail()) &&
            repository.findByEmail(clienteEntity.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un cliente con el email: " + clienteEntity.getEmail());
        }

        BeanUtils.copyProperties(clienteEntity, clienteExistente, "id");
        return repository.save(clienteExistente);
    }
}