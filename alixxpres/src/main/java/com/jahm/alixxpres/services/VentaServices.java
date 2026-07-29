package com.jahm.alixxpres.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jahm.alixxpres.modelo.ClienteEntity;
import com.jahm.alixxpres.modelo.DetalleVentaEntity;
import com.jahm.alixxpres.modelo.ProductoEntity;
import com.jahm.alixxpres.modelo.VentaEntity;
import com.jahm.alixxpres.repository.ClienteRepository;
import com.jahm.alixxpres.repository.ProductoRepository;
import com.jahm.alixxpres.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServices {
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerTodos() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public VentaEntity obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerVentasPorCliente(String email) {
        return ventaRepository.findByClienteEmail(email);
    }

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerVentasPorEstado(String estadoPago) {
        return ventaRepository.findByEstadoPago(estadoPago);
    }

    @Transactional
    public VentaEntity guardarVenta(VentaEntity venta) {
        if (venta.getCliente() == null || venta.getCliente().getId() == null) {
            throw new RuntimeException("La venta debe tener un cliente asociado");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un detalle");
        }
        return ventaRepository.save(venta);
    }

    @Transactional
    public void eliminarVenta(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
        ventaRepository.deleteById(id);
    }

    @Transactional
    public VentaEntity actualizarVenta(Long id, VentaEntity ventaEntity) {
        VentaEntity ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no existe con ID: " + id));
        BeanUtils.copyProperties(ventaEntity, ventaExistente, "id");
        return ventaRepository.save(ventaExistente);
    }

    @Transactional
    public VentaEntity procesarVenta(VentaEntity ventaRequest, String email) {
        if (ventaRequest.getDetalles() == null || ventaRequest.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe tener al menos un detalle");
        }

        ClienteEntity cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no registrado con email: " + email));

        ventaRequest.setCliente(cliente);
        ventaRequest.setFecha(LocalDateTime.now());
        ventaRequest.setEstadoPago("PENDIENTE");

        double total = 0.0;
        for (DetalleVentaEntity detalle : ventaRequest.getDetalles()) {
            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new RuntimeException("El detalle debe tener un producto valido");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor a cero");
            }

            ProductoEntity producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no existe con ID: " + detalle.getProducto().getId()));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + 
                                         ". Stock disponible: " + producto.getStock());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setVenta(ventaRequest);
            total += detalle.getSubtotal();
        }
        
        ventaRequest.setTotal(total);
        return ventaRepository.save(ventaRequest);
    }

    @Transactional
    public VentaEntity confirmarPago(Long idVenta) {
        VentaEntity venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));
        if (!"PENDIENTE".equals(venta.getEstadoPago())) {
            throw new RuntimeException("La venta ya fue procesada o cancelada. Estado actual: " + venta.getEstadoPago());
        }
        venta.setEstadoPago("PAGADO");
        return ventaRepository.save(venta);
    }

    @Transactional
    public VentaEntity cancelarVenta(Long idVenta) {
        VentaEntity venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));
        if (!"PENDIENTE".equals(venta.getEstadoPago())) {
            throw new RuntimeException("Solo se pueden cancelar ventas en estado PENDIENTE");
        }
        for (DetalleVentaEntity detalle : venta.getDetalles()) {
            ProductoEntity producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalle.getProducto().getId()));
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        venta.setEstadoPago("CANCELADO");
        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerVentasEntreFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerVentasPorClienteYEstado(String email, String estadoPago) {
        return ventaRepository.findByClienteEmailAndEstadoPago(email, estadoPago);
    }

    @Transactional(readOnly = true)
    public Double obtenerTotalVentasCliente(String email) {
        Double total = ventaRepository.sumTotalByCliente(email);
        return total != null ? total : 0.0;
    }
}
