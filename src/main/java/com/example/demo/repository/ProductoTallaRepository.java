package com.example.demo.repository;

import com.example.demo.entidades.ProductoTalla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoTallaRepository extends JpaRepository<ProductoTalla, Long> {
}