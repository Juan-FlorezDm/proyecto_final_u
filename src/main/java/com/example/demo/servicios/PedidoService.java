package com.example.demo.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entidades.DetallePedido;
import com.example.demo.entidades.Pedido;
import com.example.demo.entidades.Producto;
import com.example.demo.entidades.Usuario;
import com.example.demo.repository.PedidoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoService productoService;

    @Transactional
    public Pedido crearPedido(Usuario usuario, List<DetallePedido> detalles) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setPedido(pedido);
            
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            total = total.add(subtotal);
            
            // CORRECCIÓN: Actualizar stock con la talla específica
            productoService.actualizarStock(producto.getId(), detalle.getTalla(), detalle.getCantidad());
        }
        
        pedido.setTotal(total);
        pedido.setDetalles(detalles);
        
        return pedidoRepository.save(pedido);
    }

   public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
    return pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
}

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }
    public Optional<Pedido> obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc();
    }

    public Pedido actualizarEstadoPedido(Long pedidoId, String nuevoEstado) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            // Aquí deberías mapear el string a tu enum EstadoPedido
            // pedido.setEstado(EstadoPedido.valueOf(nuevoEstado));
            return pedidoRepository.save(pedido);
        }
        return null;
    }
}