package com.jahm.alixxpres.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.ProductoEntity;
import com.jahm.alixxpres.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;

    @Transactional(readOnly = true)
    public List<ProductoEntity> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ProductoEntity findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<ProductoEntity> findByStockLessThan(Integer stockMinimo) {
        return repository.findByStockLessThan(stockMinimo);
    }

    @Transactional(readOnly = true)
    public List<ProductoEntity> findByCategoriaId(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<ProductoEntity> findByProveedorId(Long proveedorId) {
        return repository.findByProveedorId(proveedorId);
    }

    @Transactional
    public ProductoEntity save(ProductoEntity producto) {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a cero");
        }
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        if (repository.findByNombre(producto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + producto.getNombre());
        }
        return repository.save(producto);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public ProductoEntity update(Long id, ProductoEntity productoEntity) {
        ProductoEntity productoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no existe con ID: " + id));

        if (productoEntity.getNombre() != null && !productoEntity.getNombre().trim().isEmpty()) {
            repository.findByNombre(productoEntity.getNombre())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        throw new RuntimeException("Ya existe un producto con el nombre: " + productoEntity.getNombre());
                    }
                });
        }

        if (productoEntity.getPrecio() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a cero");
        }

        if (productoEntity.getStock() != null && productoEntity.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        BeanUtils.copyProperties(productoEntity, productoExistente, "id");
        return repository.save(productoExistente);
    }

    @Transactional
    public ProductoEntity actualizarStock(Long id, Integer nuevoStock) {
        ProductoEntity producto = findById(id);
        if (nuevoStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        producto.setStock(nuevoStock);
        return repository.save(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoEntity> buscarPorTermino(String termino) {
        return repository.buscarPorTermino(termino);
    }
}
