package com.example.demo.controllers;


import com.example.demo.entidades.Usuario;
import com.example.demo.servicios.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, 
                                  BindingResult result, Model model) {
        
        // Validar errores del formulario
        if (result.hasErrors()) {
            return "registro";
        }
        
        // Verificar si el email ya existe
        if (usuarioService.existeEmail(usuario.getEmail())) {
            model.addAttribute("error", "El email ya está registrado");
            return "registro";
        }
        
        // Registrar el usuario como CLIENTE
        usuarioService.registrarCliente(usuario);
        
        return "redirect:/login?registroExitoso";
    }
}