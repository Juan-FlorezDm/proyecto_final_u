package com.example.demo.controllers;

import com.example.demo.entidades.Usuario;
import com.example.demo.entidades.Pedido;
import com.example.demo.servicios.CarritoService;
import com.example.demo.servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cliente/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private UsuarioService usuarioService;

        @GetMapping
        public String verCarrito(Authentication authentication, Model model) {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            model.addAttribute("carrito", carritoService.obtenerCarritoPorUsuario(usuario));
            return "cliente/carrito";
        }
    
   
    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam Long productoId,
            @RequestParam String talla,
            @RequestParam(defaultValue = "1") Integer cantidad,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar que la talla no esté vacía
            if (talla == null || talla.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar una talla");
                return "redirect:/cliente/tienda";
            }
            
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            carritoService.agregarAlCarrito(usuario, productoId, talla, cantidad);
            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/tienda";
    }

@PostMapping("/eliminar/{productoId}")
public String eliminarDelCarrito(
        @PathVariable Long productoId,
        @RequestParam String talla, // AGREGAR ESTE PARÁMETRO
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
    
    try {
        Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
        carritoService.eliminarDelCarrito(usuario, productoId, talla);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/cliente/carrito";
}

@PostMapping("/actualizar")
public String actualizarCantidad(
        @RequestParam Long productoId,
        @RequestParam String talla, // AGREGAR ESTE PARÁMETRO
        @RequestParam Integer cantidad,
        Authentication authentication,
        RedirectAttributes redirectAttributes) {
    
    try {
        Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
        carritoService.actualizarCantidad(usuario, productoId, talla, cantidad);
        redirectAttributes.addFlashAttribute("success", "Carrito actualizado");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }
    
    return "redirect:/cliente/carrito";
}
    
    @PostMapping("/comprar")
    public String realizarCompra(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            Pedido pedido = carritoService.realizarCompra(usuario);
            redirectAttributes.addFlashAttribute("success", "¡Compra realizada exitosamente! Número de pedido: " + pedido.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cliente/carrito";
    }
    
    private Usuario obtenerUsuarioDesdeAutenticacion(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email);
    }
}