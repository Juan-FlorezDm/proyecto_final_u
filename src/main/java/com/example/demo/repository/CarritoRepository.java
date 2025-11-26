package com.example.demo.repository;

import com.example.demo.entidades.Carrito;
import com.example.demo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}