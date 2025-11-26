package com.example.demo.servicios;

import com.example.demo.entidades.Producto;
import com.example.demo.entidades.ProductoTalla;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.ProductoTallaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ProductoTallaRepository productoTallaRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Producto> obtenerProductosConStock() {
        return productoRepository.findByTallasStockGreaterThan(0);
    }

    public long contarProductos() {
        return productoRepository.count();
    }

    public boolean existeProducto(Long id) {
        return productoRepository.existsById(id);
    }
    
    @Transactional
    public void actualizarStock(Long productoId, String talla, Integer cantidadVendida) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productoId));
        
        ProductoTalla productoTalla = producto.getTallas().stream()
                .filter(pt -> pt.getTalla().equals(talla))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Talla no encontrada para el producto"));
        
        if (productoTalla.getStock() < cantidadVendida) {
            throw new RuntimeException("Stock insuficiente para la talla: " + talla);
        }
        
        productoTalla.setStock(productoTalla.getStock() - cantidadVendida);
        productoTallaRepository.save(productoTalla);
    }
    
    public List<String> obtenerCategoriasUnicas() {
        return productoRepository.findDistinctCategorias();
    }
    
    public List<String> obtenerTallasDisponibles() {
        return List.of("XS", "S", "M", "L", "XL", "XXL", "28", "30", "32", "34", "36", "38", "40", "42", "44", "Única");
    }

     @Transactional
    public void acabarStockProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Poner todas las tallas a stock 0 y guardar cada una
        for (ProductoTalla talla : producto.getTallas()) {
            talla.setStock(0);
            productoTallaRepository.save(talla); // ✅ Esto SÍ guarda en la BD
        }
    }

    @Transactional
    public void actualizarStockTalla(Long productoId, String tallaNombre, Integer nuevoStock) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        ProductoTalla talla = producto.getTallas().stream()
                .filter(t -> t.getTalla().equals(tallaNombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Talla no encontrada"));
        
        talla.setStock(nuevoStock);
        productoTallaRepository.save(talla); // ✅ Guarda la talla individualmente
    }
}