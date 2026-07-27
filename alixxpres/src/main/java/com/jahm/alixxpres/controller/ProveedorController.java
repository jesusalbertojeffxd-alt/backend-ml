package com.jahm.alixxpres.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.modelo.ProveedorEntity;
import com.jahm.alixxpres.services.ProveedorServices;

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
@RequestMapping("/api/v1/proveedores")
@CrossOrigin(origins = "http://localhost:5173") // ✅ CAMBIADO a HTTP
@RequiredArgsConstructor
public class ProveedorController {
    private final ProveedorServices servicio;

    @GetMapping
    public ResponseEntity<List<ProveedorEntity>> listar() {
        try {
            return ResponseEntity.ok(servicio.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> obtenerDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProveedor(@PathVariable Long id) {
        try {
            servicio.eliminarProveedor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ProveedorEntity> crearProveedor(@RequestBody ProveedorEntity proveedor) {
        try {
            ProveedorEntity proveedorGuardado = servicio.guardarProveedor(proveedor);
            return new ResponseEntity<>(proveedorGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProveedor(@PathVariable Long id, @RequestBody ProveedorEntity proveedor) {
        try {
            ProveedorEntity proveedorActualizado = servicio.actualizarProveedor(id, proveedor);
            return ResponseEntity.ok(proveedorActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}