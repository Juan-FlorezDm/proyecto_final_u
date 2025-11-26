package com.example.demo.entidades;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();
    
    // Constructor
    public Carrito() {}
    
    public Carrito(Usuario usuario) {
        this.usuario = usuario;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public List<ItemCarrito> getItems() { return items; }
    public void setItems(List<ItemCarrito> items) { this.items = items; }
    
    // MÉTODOS CORREGIDOS:
    public void agregarItem(Producto producto, String talla, Integer cantidad) {
        ItemCarrito itemExistente = items.stream()
                .filter(item -> item.getProducto().getId().equals(producto.getId()) && item.getTalla().equals(talla))
                .findFirst()
                .orElse(null);
                
        if (itemExistente != null) {
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito(this, producto, talla, cantidad);
            items.add(nuevoItem);
        }
    }

    public void eliminarItem(Long productoId, String talla) {
        items.removeIf(item -> item.getProducto().getId().equals(productoId) && item.getTalla().equals(talla));
    }

    public void actualizarCantidad(Long productoId, String talla, Integer cantidad) {
        items.stream()
                .filter(item -> item.getProducto().getId().equals(productoId) && item.getTalla().equals(talla))
                .findFirst()
                .ifPresent(item -> item.setCantidad(cantidad));
    }
    
    public void limpiarCarrito() {
        items.clear();
    }
    
    public BigDecimal getTotal() {
        return items.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public Integer getTotalItems() {
        return items.stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
    }
}