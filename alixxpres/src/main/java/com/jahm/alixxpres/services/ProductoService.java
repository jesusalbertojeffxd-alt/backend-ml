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

    // Obtener todos los productos
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener producto por id
    @Transactional(readOnly = true)
    public ProductoEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    // Obtener productos por nombre (búsqueda)
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    // Obtener productos con stock bajo
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerProductosConStockBajo(Integer stockMinimo) {
        return repository.findByStockLessThan(stockMinimo);
    }

    // Obtener productos por categoría
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerPorCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }

    // Obtener productos por proveedor
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerPorProveedor(Long proveedorId) {
        return repository.findByProveedorId(proveedorId);
    }

    // Guardar producto
    @Transactional
    public ProductoEntity guardarProducto(ProductoEntity producto) {
        // Validaciones
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del producto es obligatorio");
        }
        
        if (producto.getPrecio() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a cero");
        }
        
        if (producto.getStock() == null || producto.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        // Verificar si ya existe un producto con el mismo nombre
        if (repository.findByNombre(producto.getNombre()).isPresent()) {
            throw new RuntimeException("Ya existe un producto con el nombre: " + producto.getNombre());
        }

        return repository.save(producto);
    }

    // Eliminar producto
    @Transactional
    public void eliminarProducto(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Actualizar producto
    @Transactional
    public ProductoEntity actualizarProducto(Long id, ProductoEntity productoEntity) {
        ProductoEntity productoExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no existe con ID: " + id));

        // Validaciones para actualización
        if (productoEntity.getNombre() != null && !productoEntity.getNombre().trim().isEmpty()) {
            // Verificar si el nuevo nombre ya está en uso por otro producto
            repository.findByNombre(productoEntity.getNombre())
                .ifPresent(p -> {
                    if (!p.getId().equals(id)) {
                        throw new RuntimeException("Ya existe un producto con el nombre: " + productoEntity.getNombre());
                    }
                });
        }

        // ✅ CORREGIDO: Eliminado el '!= null' porque precio es double (primitivo)
        if (productoEntity.getPrecio() <= 0) {
            throw new RuntimeException("El precio del producto debe ser mayor a cero");
        }

        if (productoEntity.getStock() != null && productoEntity.getStock() < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }

        BeanUtils.copyProperties(productoEntity, productoExistente, "id");
        return repository.save(productoExistente);
    }

    // Actualizar stock de un producto
    @Transactional
    public ProductoEntity actualizarStock(Long id, Integer nuevoStock) {
        ProductoEntity producto = obtenerPorId(id);
        if (nuevoStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        producto.setStock(nuevoStock);
        return repository.save(producto);
    }

    // Buscar productos por término (nombre o descripción)
    @Transactional(readOnly = true)
    public List<ProductoEntity> buscarPorTermino(String termino) {
        return repository.buscarPorTermino(termino);
    }
}