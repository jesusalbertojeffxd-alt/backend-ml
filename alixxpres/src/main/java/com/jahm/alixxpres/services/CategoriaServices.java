package com.jahm.alixxpres.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.CategoriaEntity;
import com.jahm.alixxpres.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaServices {
    private final CategoriaRepository repository;

    // Obtener todas las categorias
    @Transactional(readOnly = true)
    public List<CategoriaEntity> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener categoria por id
    @Transactional(readOnly = true)
    public CategoriaEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con ID: " + id));
    }

    // Guardar categoria
    @Transactional
    public CategoriaEntity guardarCategoria(CategoriaEntity categoria) {
        // Aqui pueden ir todas las validaciones
        return repository.save(categoria);
    }

    // Eliminar categoria
    @Transactional
    public void eliminarCategoria(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Categoria no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Actualizar categoria
    @Transactional
    public CategoriaEntity actualizarCategoria(Long id, CategoriaEntity categoriaEntity) {
        CategoriaEntity categoriaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria no existe con ID: " + id));

        BeanUtils.copyProperties(categoriaEntity, categoriaExistente, "id");
        return repository.save(categoriaExistente);
    }
}