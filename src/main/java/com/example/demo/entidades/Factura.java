package com.example.demo.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Chunk;

@Entity
@Table(name = "facturas")
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numeroFactura;
    
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @Column(nullable = false)
    private LocalDateTime fechaEmision;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal impuestos;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = false)
    private String estado; // EMITIDA, CANCELADA
    
    // Información de la empresa
    @Column(nullable = false)
    private String empresaNombre = "Tienda de Ropa S.A.S";
    
    @Column(nullable = false)
    private String empresaNit = "900.123.456-7";
    
    @Column(nullable = false)
    private String empresaDireccion = "Calle 123 #45-67, Bogotá, Colombia";
    
    @Column(nullable = false)
    private String empresaTelefono = "+57 1 1234567";
    
    @Column(nullable = false)
    private String empresaEmail = "info@tiendaropa.com";
    
    // Constructores
    public Factura() {
        this.fechaEmision = LocalDateTime.now();
        this.estado = "EMITIDA";
    }
    
    public Factura(Pedido pedido) {
        this();
        this.pedido = pedido;
        this.subtotal = pedido.getTotal();
        this.impuestos = BigDecimal.ZERO; // O calcular impuestos si aplica
        this.total = this.subtotal.add(this.impuestos);
        this.numeroFactura = generarNumeroFactura();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getImpuestos() { return impuestos; }
    public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    // Método para generar número de factura único
    private String generarNumeroFactura() {
        return "FACT-" + System.currentTimeMillis();
    }
    
    // Método para obtener items de la factura (desde el pedido)
    public List<DetallePedido> getItems() {
        return pedido != null ? pedido.getDetalles() : new ArrayList<>();
    }

}