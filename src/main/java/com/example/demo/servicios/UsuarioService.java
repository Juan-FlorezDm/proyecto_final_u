package com.example.demo.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.entidades.Usuario;
import com.example.demo.entidades.Rol;
import com.example.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registrarCliente(Usuario usuario) {
        // Codificar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // Asignar rol CLIENTE automáticamente
        usuario.setRol(Rol.CLIENTE);
        usuarioRepository.save(usuario);
    }

    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
    
    // Método opcional para buscar usuario por email
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    public void registrarAdmin(Usuario usuario) {
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.setRol(Rol.ADMIN);
    usuarioRepository.save(usuario);
}
}