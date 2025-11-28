package com.example.demo.servicios;

import com.example.demo.entidades.*;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.repository.ItemCarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private PedidoService pedidoService;

    public Carrito obtenerCarritoPorUsuario(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarrito(usuario));
    }
    
    private Carrito crearCarrito(Usuario usuario) {
        Carrito carrito = new Carrito(usuario);
        return carritoRepository.save(carrito);
    }
    
    @Transactional
    public void agregarAlCarrito(Usuario usuario, Long productoId, String talla, Integer cantidad) {
        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
        // Verificar si la talla tiene stock suficiente
        if (!producto.tieneStockDisponible(talla, cantidad)) {
            throw new RuntimeException("Stock insuficiente para la talla " + talla);
        }
        
        Carrito carrito = obtenerCarritoPorUsuario(usuario);
        carrito.agregarItem(producto, talla, cantidad);
        carritoRepository.save(carrito);
    }
    
    @Transactional
    public void eliminarDelCarrito(Usuario usuario, Long productoId, String talla) {
        Carrito carrito = obtenerCarritoPorUsuario(usuario);
        carrito.eliminarItem(productoId, talla);
        carritoRepository.save(carrito);
    }
    
    @Transactional
    public void actualizarCantidad(Usuario usuario, Long productoId, String talla, Integer cantidad) {
        if (cantidad <= 0) {
            eliminarDelCarrito(usuario, productoId, talla);
            return;
        }
        
        Producto producto = productoService.obtenerPorId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                
        if (!producto.tieneStockDisponible(talla, cantidad)) {
            throw new RuntimeException("Stock insuficiente para la talla " + talla);
        }
        
        Carrito carrito = obtenerCarritoPorUsuario(usuario);
        carrito.actualizarCantidad(productoId, talla, cantidad);
        carritoRepository.save(carrito);
    }
    
    @Transactional
    public void limpiarCarrito(Usuario usuario) {
        Carrito carrito = obtenerCarritoPorUsuario(usuario);
        carrito.limpiarCarrito();
        carritoRepository.save(carrito);
    }
    
    @Transactional
public Pedido realizarCompra(Usuario usuario) {
    Carrito carrito = obtenerCarritoPorUsuario(usuario);
    
    if (carrito.getItems().isEmpty()) {
        throw new RuntimeException("El carrito está vacío");
    }
    
    // Convertir items del carrito a detalles de pedido CON TALLA
    List<DetallePedido> detalles = carrito.getItems().stream()
            .map(item -> new DetallePedido(item.getProducto(), item.getTalla(), item.getCantidad()))
            .toList();
    
    // Crear el pedido
    Pedido pedido = pedidoService.crearPedido(usuario, detalles);
    
    // Generar factura automáticamente
    facturaService.generarFactura(pedido);
    
    // Limpiar el carrito
    limpiarCarrito(usuario);
    
    return pedido;
}
}