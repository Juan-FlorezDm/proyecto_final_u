package com.example.demo.repository;

import com.example.demo.entidades.Factura;
import com.example.demo.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByPedido(Pedido pedido);
    Optional<Factura> findByNumeroFactura(String numeroFactura);
}