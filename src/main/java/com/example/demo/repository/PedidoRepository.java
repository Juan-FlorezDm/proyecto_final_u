package com.example.demo.repository;

import com.example.demo.entidades.Pedido;
import com.example.demo.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(Usuario usuario);
    
    List<Pedido> findAllByOrderByFechaPedidoDesc();
    
    List<Pedido> findByUsuarioId(Long usuarioId);
}