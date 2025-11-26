package com.example.demo.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_pedido")
public class DetallePedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private String talla; // NUEVO CAMPO
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    // Constructores
    public DetallePedido() {}
    
    public DetallePedido(Producto producto, String talla, Integer cantidad) {
        this.producto = producto;
        this.talla = talla;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    // Método helper para calcular subtotal
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}