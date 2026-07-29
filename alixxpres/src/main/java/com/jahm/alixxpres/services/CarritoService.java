package com.jahm.alixxpres.services;

import com.jahm.alixxpres.dto.CarritoDTO;
import com.jahm.alixxpres.dto.CarritoItemDTO;
import com.jahm.alixxpres.modelo.CarritoEntity;
import com.jahm.alixxpres.modelo.CarritoItemEntity;
import com.jahm.alixxpres.modelo.ProductoEntity;
import com.jahm.alixxpres.modelo.UsuarioEntity;
import com.jahm.alixxpres.repository.CarritoRepository;
import com.jahm.alixxpres.repository.ProductoRepository;
import com.jahm.alixxpres.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoService {
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public CarritoDTO getCarritoByUsername(String username) {
        CarritoEntity carrito = carritoRepository.findByUsuarioUsername(username)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario: " + username));
        return convertToDTO(carrito);
    }
    
    public CarritoDTO getOrCreateCarrito(String username) {
        Optional<CarritoEntity> carritoOpt = carritoRepository.findByUsuarioUsername(username);
        
        if (carritoOpt.isPresent()) {
            return convertToDTO(carritoOpt.get());
        } else {
            UsuarioEntity usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
            
            CarritoEntity nuevoCarrito = new CarritoEntity();
            nuevoCarrito.setUsuario(usuario);
            nuevoCarrito = carritoRepository.save(nuevoCarrito);
            return convertToDTO(nuevoCarrito);
        }
    }
    
    public CarritoDTO agregarProducto(String username, Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        
        ProductoEntity producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + producto.getStock());
        }
        
        CarritoEntity carrito = carritoRepository.findByUsuarioId(usuario.getId())
            .orElseGet(() -> {
                CarritoEntity nuevoCarrito = new CarritoEntity();
                nuevoCarrito.setUsuario(usuario);
                return carritoRepository.save(nuevoCarrito);
            });
        
        Optional<CarritoItemEntity> itemExistente = carrito.getItems().stream()
            .filter(item -> item.getProducto().getId().equals(productoId))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            CarritoItemEntity item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (nuevaCantidad > producto.getStock()) {
                throw new RuntimeException("No puedes agregar mas unidades. Stock disponible: " + producto.getStock());
            }
            item.setCantidad(nuevaCantidad);
        } else {
            CarritoItemEntity nuevoItem = new CarritoItemEntity();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(cantidad);
            carrito.getItems().add(nuevoItem);
        }
        
        carrito.setFechaActualizacion(LocalDateTime.now());
        carrito = carritoRepository.save(carrito);
        return convertToDTO(carrito);
    }
    
    public CarritoDTO actualizarCantidad(String username, Long productoId, Integer cantidad) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        
        CarritoEntity carrito = carritoRepository.findByUsuarioId(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        if (cantidad <= 0) {
            carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));
        } else {
            CarritoItemEntity item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
            
            if (cantidad > item.getProducto().getStock()) {
                throw new RuntimeException("Stock insuficiente. Stock disponible: " + item.getProducto().getStock());
            }
            item.setCantidad(cantidad);
        }
        
        carrito.setFechaActualizacion(LocalDateTime.now());
        carrito = carritoRepository.save(carrito);
        return convertToDTO(carrito);
    }
    
    public CarritoDTO eliminarProducto(String username, Long productoId) {
        return actualizarCantidad(username, productoId, 0);
    }
    
    public void vaciarCarrito(String username) {
        UsuarioEntity usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        
        CarritoEntity carrito = carritoRepository.findByUsuarioId(usuario.getId())
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
        
        carrito.getItems().clear();
        carrito.setFechaActualizacion(LocalDateTime.now());
        carritoRepository.save(carrito);
    }
    
    private CarritoDTO convertToDTO(CarritoEntity carrito) {
        List<CarritoItemDTO> itemsDTO = new ArrayList<>();
        double total = 0.0;
        int totalItems = 0;
        
        for (CarritoItemEntity item : carrito.getItems()) {
            ProductoEntity producto = item.getProducto();
            double subtotal = producto.getPrecio() * item.getCantidad();
            total += subtotal;
            totalItems += item.getCantidad();
            
            CarritoItemDTO itemDTO = new CarritoItemDTO(
                item.getId(),
                producto,
                item.getCantidad(),
                subtotal
            );
            itemsDTO.add(itemDTO);
        }
        
        return new CarritoDTO(carrito.getId(), itemsDTO, total, totalItems);
    }
}