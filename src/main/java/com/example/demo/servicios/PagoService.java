package com.example.demo.servicios;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entidades.Carrito;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;


@Service
public class PagoService {

    @Value("${stripe.secret.key}")
    private String secretKey;
    
    @Value("${stripe.currency}")
    private String currency;

    public Map<String, String> crearPaymentIntent(Carrito carrito) throws StripeException {
        Stripe.apiKey = secretKey;
        
        // Validar que el carrito no esté vacío
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // Convertir a centavos (Stripe requiere amount en la moneda más pequeña)
        long amount = carrito.getTotal().multiply(BigDecimal.valueOf(100)).longValue();
        
        // Validar que el amount sea mayor a 0
        if (amount <= 0) {
            throw new RuntimeException("El monto total debe ser mayor a 0");
        }
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        response.put("paymentIntentId", paymentIntent.getId());
        
        return response;
    }
    
    public boolean verificarPagoExitoso(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return "succeeded".equals(paymentIntent.getStatus());
    }
}