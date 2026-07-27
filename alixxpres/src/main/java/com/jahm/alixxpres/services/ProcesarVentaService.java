package com.jahm.alixxpres.services;

import org.springframework.stereotype.Service;

import com.jahm.alixxpres.modelo.DetalleVentaEntity;
import com.jahm.alixxpres.modelo.ProductoEntity;
import com.jahm.alixxpres.modelo.VentaEntity;
import com.jahm.alixxpres.repository.ProductoRepository;
import com.jahm.alixxpres.repository.VentaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesarVentaService {
    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public VentaEntity procesarVenta(VentaEntity ventaRequest) {
        // Validar que la venta tenga detalles
        if (ventaRequest.getDetalles() == null || ventaRequest.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un detalle");
        }

        ventaRequest.setFecha(java.time.LocalDateTime.now());
        ventaRequest.setEstadoPago("PENDIENTE");

        // Calcular totales y descontar el stock
        Double total = 0.0;
        for (DetalleVentaEntity detalle : ventaRequest.getDetalles()) {
            // Validar que el detalle tenga un producto
            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new RuntimeException("El detalle debe tener un producto válido");
            }

            // Validar que la cantidad sea positiva
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a cero");
            }

            ProductoEntity producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalle.getProducto().getId()));

            // Validar que haya suficiente stock
            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + 
                                         ". Stock disponible: " + producto.getStock());
            }

            // Actualizar el stock
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            // Calcular precios y subtotal del detalle
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setVenta(ventaRequest);
            total += detalle.getSubtotal();
        }

        ventaRequest.setTotal(total);
        return ventaRepository.save(ventaRequest);
    }
}