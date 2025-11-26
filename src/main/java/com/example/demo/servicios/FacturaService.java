package com.example.demo.servicios;

import com.example.demo.entidades.*;
import com.example.demo.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;
    
    @Autowired
    private PedidoService pedidoService;

    @Transactional
    public Factura generarFactura(Pedido pedido) {
        try {
            // Verificar si ya existe una factura para este pedido
            Optional<Factura> facturaExistente = facturaRepository.findByPedido(pedido);
            if (facturaExistente.isPresent()) {
                return facturaExistente.get();
            }
            
            // Crear nueva factura
            Factura factura = new Factura(pedido);
            return facturaRepository.save(factura);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar factura: " + e.getMessage(), e);
        }
    }
    
    public Factura obtenerFacturaPorPedido(Long pedidoId) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
            
            return facturaRepository.findByPedido(pedido)
                    .orElseThrow(() -> new RuntimeException("No se encontró factura para el pedido: " + pedidoId));
                    
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener factura: " + e.getMessage(), e);
        }
    }
}