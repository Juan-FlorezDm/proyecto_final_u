package com.example.demo.controllers;

import com.example.demo.servicios.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/tienda")
    public String tienda(Model model) {
        // Obtener todos los productos disponibles
        model.addAttribute("productos", productoService.obtenerProductosConStock());
        model.addAttribute("titulo", "Tienda de Ropa");
        return "cliente/tienda";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "cliente/perfil";
    }
}