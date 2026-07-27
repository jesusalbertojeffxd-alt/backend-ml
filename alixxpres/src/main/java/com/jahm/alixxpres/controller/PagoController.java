package com.jahm.alixxpres.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jahm.alixxpres.dto.PagoRequest;
import com.jahm.alixxpres.modelo.VentaEntity;
import com.jahm.alixxpres.services.VentaServices;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pagos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PagoController {
    @Value("${stripe.apikey.secret}")
    private String stripeSecretKey;

    private final VentaServices ventaService;

    @PostMapping("/crear-intencion")
    public ResponseEntity<?> crearIntencion(@RequestBody PagoRequest peticion) {
        try {
            Stripe.apiKey = stripeSecretKey;
            VentaEntity venta = ventaService.obtenerPorId(peticion.getIdVenta());
            long montoCentavos = (long) (venta.getTotal() * 100);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(montoCentavos)
                    .setCurrency(peticion.getMoneda() != null ? peticion.getMoneda() : "mxn")
                    .putMetadata("id_venta", venta.getId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("clientSecret", intent.getClientSecret());

            return ResponseEntity.ok(respuesta);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Error al crear la intención de pago",
                "mensaje", ex.getMessage()
            ));
        }
    }

    @PostMapping("/confirmar-pago/{idVenta}")
    public ResponseEntity<?> confirmarPago(@PathVariable Long idVenta) {
        try {
            VentaEntity ventaActualizada = ventaService.confirmarPago(idVenta);
            return ResponseEntity.ok(ventaActualizada);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "Error al confirmar el pago",
                "mensaje", ex.getMessage()
            ));
        }
    }
}