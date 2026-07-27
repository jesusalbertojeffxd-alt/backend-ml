package com.jahm.alixxpres.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.modelo.ClienteEntity;
import com.jahm.alixxpres.services.ClienteServices;

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
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "http://localhost:5173") // ✅ CAMBIADO a HTTP
@RequiredArgsConstructor
public class ClienteController {
    private final ClienteServices servicio;

    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listar() {
        try {
            return ResponseEntity.ok(servicio.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> obtenerDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCliente(@PathVariable Long id) {
        try {
            servicio.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<ClienteEntity> crearCliente(@RequestBody ClienteEntity cliente) {
        try {
            ClienteEntity clienteGuardado = servicio.guardarCliente(cliente);
            return new ResponseEntity<>(clienteGuardado, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Long id, @RequestBody ClienteEntity cliente) {
        try {
            ClienteEntity clienteActualizado = servicio.actualizarCliente(id, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}