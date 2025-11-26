package com.example.demo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String categoria;
    
    @Column(name = "imagen_url")
    private String imagenUrl;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductoTalla> tallas = new ArrayList<>();
    
    // Constructores
    public Producto() {}
    
    public Producto(String nombre, String descripcion, BigDecimal precio, 
                   String categoria, String imagenUrl) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public List<ProductoTalla> getTallas() { return tallas; }
    public void setTallas(List<ProductoTalla> tallas) { this.tallas = tallas; }
    
    // Métodos helper para manejar tallas
    public void agregarTalla(String talla, Integer stock) {
        ProductoTalla productoTalla = new ProductoTalla(this, talla, stock);
        this.tallas.add(productoTalla);
    }
    
    public void eliminarTalla(String talla) {
        this.tallas.removeIf(pt -> pt.getTalla().equals(talla));
    }
    
    public Integer getStockTotal() {
        return this.tallas.stream()
                .mapToInt(ProductoTalla::getStock)
                .sum();
    }
    
    public boolean tieneStockDisponible(String talla, Integer cantidad) {
        return this.tallas.stream()
                .filter(pt -> pt.getTalla().equals(talla))
                .findFirst()
                .map(pt -> pt.getStock() >= cantidad)
                .orElse(false);
    }

}