package com.example.demo.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_tallas")
public class ProductoTalla {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private String talla;
    
    @Column(nullable = false)
    private Integer stock;
    
    // Constructores
    public ProductoTalla() {}
    
    public ProductoTalla(Producto producto, String talla, Integer stock) {
        this.producto = producto;
        this.talla = talla;
        this.stock = stock;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
    
    public String getTalla() { return talla; }
    public void setTalla(String talla) { this.talla = talla; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}