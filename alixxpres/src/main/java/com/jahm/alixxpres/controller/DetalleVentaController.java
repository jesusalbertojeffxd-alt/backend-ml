package com.jahm.alixxpres.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.modelo.DetalleVentaEntity;
import com.jahm.alixxpres.services.DetalleVentaServices;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/detalles-venta")
@CrossOrigin(origins = "http://localhost:5173") // ✅ CAMBIADO a HTTP
@RequiredArgsConstructor
public class DetalleVentaController {
    private final DetalleVentaServices servicio;

    @GetMapping
    public ResponseEntity<List<DetalleVentaEntity>> listar() {
        try {
            return ResponseEntity.ok(servicio.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaEntity> obtenerDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarDetalleVenta(@PathVariable Long id) {
        try {
            servicio.eliminarDetalleVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<DetalleVentaEntity> crearDetalleVenta(@RequestBody DetalleVentaEntity detalleVenta) {
        try {
            DetalleVentaEntity detalleVentaGuardado = servicio.guardarDetalleVenta(detalleVenta);
            return new ResponseEntity<>(detalleVentaGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVentaEntity detalleVenta) {
        try {
            DetalleVentaEntity detalleVentaActualizado = servicio.actualizarDetalleVenta(id, detalleVenta);
            return ResponseEntity.ok(detalleVentaActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}