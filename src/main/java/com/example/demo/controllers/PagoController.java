package com.example.demo.controllers;

import com.example.demo.entidades.*;
import com.example.demo.servicios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.stripe.exception.StripeException;

import java.util.Map;

@Controller
@RequestMapping("/cliente/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;
    
    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Value("${stripe.public.key}")
    private String stripePublicKey;

    // Endpoint GET para mostrar la página de pago
    @GetMapping
    public String mostrarPago(Authentication authentication, Model model) {
        try {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
            
            if (carrito.getItems().isEmpty()) {
                return "redirect:/cliente/carrito";
            }
            
            // Crear el PaymentIntent aquí y pasar el clientSecret directamente
            Map<String, String> paymentIntent = pagoService.crearPaymentIntent(carrito);
            
            model.addAttribute("carrito", carrito);
            model.addAttribute("stripePublicKey", stripePublicKey);
            model.addAttribute("clientSecret", paymentIntent.get("clientSecret"));
            
            return "cliente/pago";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/cliente/carrito";
        }
    }
    
    // Endpoint alternativo para crear PaymentIntent (API) - mantener por si acaso
    @PostMapping("/crear-payment-intent")
    @ResponseBody
    public ResponseEntity<?> crearPaymentIntent(Authentication authentication) {
        try {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            Carrito carrito = carritoService.obtenerCarritoPorUsuario(usuario);
            
            Map<String, String> paymentIntent = pagoService.crearPaymentIntent(carrito);
            return ResponseEntity.ok(paymentIntent);
            
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Endpoint para procesar pago exitoso (GET para redirección de Stripe)
    @GetMapping("/exito")
    public String pagoExito(@RequestParam String payment_intent, 
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            // Verificar que el pago fue exitoso
            if (pagoService.verificarPagoExitoso(payment_intent)) {
                Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
                
                // Crear el pedido
                Pedido pedido = carritoService.realizarCompra(usuario);
                
                redirectAttributes.addFlashAttribute("success", 
                    "¡Pago realizado exitosamente! Número de pedido: " + pedido.getId());
            } else {
                redirectAttributes.addFlashAttribute("error", "El pago no se ha completado correctamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
        }
        
        return "redirect:/cliente/facturas";
    }
    
    @GetMapping("/cancelado")
    public String pagoCancelado(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warning", "El pago fue cancelado");
        return "redirect:/cliente/carrito";
    }
    
    private Usuario obtenerUsuarioDesdeAutenticacion(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email);
    }
}