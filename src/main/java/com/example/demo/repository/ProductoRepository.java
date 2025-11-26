package com.example.demo.repository;

import com.example.demo.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findByCategoria(String categoria);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT DISTINCT p FROM Producto p JOIN p.tallas t WHERE t.stock > 0")
    List<Producto> findByTallasStockGreaterThan(Integer stock);
    
    @Query("SELECT DISTINCT p.categoria FROM Producto p")
    List<String> findDistinctCategorias();
}