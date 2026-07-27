package com.jahm.alixxpres.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.DetalleVentaEntity;
import com.jahm.alixxpres.repository.DetalleVentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleVentaServices {
    private final DetalleVentaRepository repository;

    // Obtener todos los detalles de ventas
    @Transactional(readOnly = true)
    public List<DetalleVentaEntity> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener DetalleVenta por id
    @Transactional(readOnly = true)
    public DetalleVentaEntity obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleVenta no encontrado con ID: " + id));
    }

    // Obtener detalles por ID de venta
    @Transactional(readOnly = true)
    public List<DetalleVentaEntity> obtenerPorVentaId(Long ventaId) {
        return repository.findByVentaId(ventaId);
    }

    // Obtener detalles por ID de producto
    @Transactional(readOnly = true)
    public List<DetalleVentaEntity> obtenerPorProductoId(Long productoId) {
        return repository.findByProductoId(productoId);
    }

    // Guardar DetalleVenta
    @Transactional
    public DetalleVentaEntity guardarDetalleVenta(DetalleVentaEntity detalle) {
        // Calcular subtotal automáticamente
        if (detalle.getCantidad() != null && detalle.getPrecioUnitario() != null) {
            detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
        }
        return repository.save(detalle);
    }

    // Eliminar DetalleVenta
    @Transactional
    public void eliminarDetalleVenta(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("DetalleVenta no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }

    // Actualizar DetalleVenta
    @Transactional
    public DetalleVentaEntity actualizarDetalleVenta(Long id, DetalleVentaEntity detalleVentaEntity) {
        DetalleVentaEntity detalleVentaExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DetalleVenta no existe con ID: " + id));

        // Calcular subtotal automáticamente si se actualizan cantidad o precio
        if (detalleVentaEntity.getCantidad() != null && detalleVentaEntity.getPrecioUnitario() != null) {
            detalleVentaEntity.setSubtotal(detalleVentaEntity.getCantidad() * detalleVentaEntity.getPrecioUnitario());
        }

        BeanUtils.copyProperties(detalleVentaEntity, detalleVentaExistente, "id");
        return repository.save(detalleVentaExistente);
    }
}