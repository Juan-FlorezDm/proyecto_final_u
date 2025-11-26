package com.example.demo.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private String talla; // AGREGAR ESTE CAMPO
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    // Constructores
    public ItemCarrito() {}
    
    public ItemCarrito(Carrito carrito, Producto producto, String talla, Integer cantidad) {
        this.carrito = carrito;
        this.producto = producto;
        this.talla = talla;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Carrito getCarrito() { return carrito; }
    public void setCarrito(Carrito carrito) { this.carrito = carrito; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public String getTalla() { return talla; } // AGREGAR ESTE GETTER
    public void setTalla(String talla) { this.talla = talla; } // AGREGAR ESTE SETTER
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    
    // Método para calcular subtotal
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}