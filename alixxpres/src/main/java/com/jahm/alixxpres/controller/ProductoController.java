package com.jahm.alixxpres.controller;

import com.jahm.alixxpres.modelo.ProductoEntity;
import com.jahm.alixxpres.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(
    origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:3000",
        "http://127.0.0.1:5173",
        "http://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io:5173",
        "http://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io:5173",
        "http://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io",
        "http://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io"
    },
    allowedHeaders = "*",
    allowCredentials = "true"
)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoEntity>> getAllProductos() {
        try {
            List<ProductoEntity> productos = productoService.findAll();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> getProductoById(@PathVariable Long id) {
        try {
            ProductoEntity producto = productoService.findById(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createProducto(@RequestBody ProductoEntity producto) {
        try {
            ProductoEntity nuevoProducto = productoService.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateProducto(@PathVariable Long id, @RequestBody ProductoEntity producto) {
        try {
            producto.setId(id);
            ProductoEntity productoActualizado = productoService.save(producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar producto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        try {
            productoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar producto: " + e.getMessage());
        }
    }
}
