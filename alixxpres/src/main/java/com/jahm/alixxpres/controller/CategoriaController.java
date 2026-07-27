package com.jahm.alixxpres.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.modelo.CategoriaEntity;
import com.jahm.alixxpres.services.CategoriaServices;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/categorias")
@CrossOrigin(origins = "http://localhost:5173") // ✅ CORREGIDO a HTTP
@RequiredArgsConstructor
public class CategoriaController {
    private final CategoriaServices servicio;

    @GetMapping
    public ResponseEntity<List<CategoriaEntity>> listar() {
        try {
            return ResponseEntity.ok(servicio.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEntity> obtenerDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        try {
            servicio.eliminarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<CategoriaEntity> crear(@RequestBody CategoriaEntity categoria) {
        try {
            CategoriaEntity nuevo = servicio.guardarCategoria(categoria);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(@PathVariable Long id, @RequestBody CategoriaEntity categoria) {
        try {
            CategoriaEntity categoriaActualizado = servicio.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}