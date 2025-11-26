package com.example.demo.repository;

import com.example.demo.entidades.ItemCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCarritoRepository extends JpaRepository<ItemCarrito, Long> {
    void deleteByCarritoId(Long carritoId);
}