package com.jahm.alixxpres.controller;

import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.modelo.VentaEntity;
import com.jahm.alixxpres.services.VentaServices;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/ventas")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:5174",
    "http://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io:5173",
    "http://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io:5173"
})
@RequiredArgsConstructor
public class VentaController {
    
    private final VentaServices servicio;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<VentaEntity>> listar() {
        try {
            return ResponseEntity.ok(servicio.obtenerTodos());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CLIENTE')")
    public ResponseEntity<VentaEntity> obtenerDetalles(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(servicio.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> eliminarVenta(@PathVariable Long id) {
        try {
            servicio.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<?> crearVenta(@RequestBody VentaEntity venta, Principal principal) {
        try {
            System.out.println("=== CREANDO VENTA ===");
            System.out.println("Venta recibida: " + venta);
            System.out.println("Principal: " + principal);
            
            String username = principal.getName();
            System.out.println("Username: " + username);
            
            VentaEntity nuevaVenta = servicio.procesarVenta(venta, username);
            System.out.println("Venta creada: " + nuevaVenta);
            
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> actualizarVenta(@PathVariable Long id, @RequestBody VentaEntity venta) {
        try {
            VentaEntity ventaActualizado = servicio.actualizarVenta(id, venta);
            return ResponseEntity.ok(ventaActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/mis-compras")
    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    public ResponseEntity<List<VentaEntity>> listarMisCompras(Principal principal) {
        try {
            String username = principal.getName();
            List<VentaEntity> ventas = servicio.obtenerVentasPorCliente(username);
            return ResponseEntity.ok(ventas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
