package com.jahm.alixxpres.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.ProveedorEntity;
import com.jahm.alixxpres.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProveedorServices {
    private final ProveedorRepository repository;

    // Obtener todos los Proveedores
    @Transactional(readOnly = true)
    public List<ProveedorEntity> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener Proveedor por id
    @Transactional(readOnly = true)
    public ProveedorEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + id));
    }

    // Obtener Proveedor por email
    @Transactional(readOnly = true)
    public ProveedorEntity obtenerPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con email: " + email));
    }

    // Obtener Proveedores por nombre (búsqueda)
    @Transactional(readOnly = true)
    public List<ProveedorEntity> obtenerPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    // Buscar proveedores por término
    @Transactional(readOnly = true)
    public List<ProveedorEntity> buscarPorTermino(String termino) {
        return repository.buscarPorTermino(termino);
    }

    // Guardar Proveedor
    @Transactional
    public ProveedorEntity guardarProveedor(ProveedorEntity proveedor) {
        // Validaciones
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del proveedor es obligatorio");
        }

        // Validar email único
        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (repository.findByEmail(proveedor.getEmail()).isPresent()) {
                throw new RuntimeException("Ya existe un proveedor con el email: " + proveedor.getEmail());
            }
        }

        // Validar nombre único
        if (repository.findByNombre(proveedor.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un proveedor con el nombre: " + proveedor.getNombre());
        }

        return repository.save(proveedor);
    }

    // Eliminar Proveedor
    @Transactional
    public void eliminarProveedor(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Proveedor no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Actualizar Proveedor
    @Transactional
    public ProveedorEntity actualizarProveedor(Long id, ProveedorEntity proveedorEntity) {
        ProveedorEntity proveedorExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no existe con ID: " + id));

        // Validar email único en actualización
        if (proveedorEntity.getEmail() != null && !proveedorEntity.getEmail().trim().isEmpty()) {
            if (!proveedorEntity.getEmail().equals(proveedorExistente.getEmail())) {
                repository.findByEmail(proveedorEntity.getEmail())
                    .ifPresent(p -> {
                        throw new RuntimeException("Ya existe un proveedor con el email: " + proveedorEntity.getEmail());
                    });
            }
        }

        // Validar nombre único en actualización
        if (proveedorEntity.getNombre() != null && !proveedorEntity.getNombre().trim().isEmpty()) {
            if (!proveedorEntity.getNombre().equals(proveedorExistente.getNombre())) {
                repository.findByNombre(proveedorEntity.getNombre())
                    .ifPresent(p -> {
                        throw new RuntimeException("Ya existe un proveedor con el nombre: " + proveedorEntity.getNombre());
                    });
            }
        }

        BeanUtils.copyProperties(proveedorEntity, proveedorExistente, "id");
        return repository.save(proveedorExistente);
    }

    // Verificar si existe un proveedor por email
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return repository.existsByEmail(email);
    }

    // Verificar si existe un proveedor por nombre
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }
}