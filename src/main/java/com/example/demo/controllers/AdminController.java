package com.example.demo.controllers;

import com.example.demo.entidades.Producto;
import com.example.demo.entidades.ProductoTalla;
import com.example.demo.servicios.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProductos", productoService.contarProductos());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String listarProductos(Model model) {
        List<Producto> productos = productoService.obtenerTodos();
        
        // Calcular el stock total para cada producto
        Map<Long, Integer> stockTotales = new HashMap<>();
        for (Producto producto : productos) {
            int stockTotal = producto.getTallas().stream()
                    .mapToInt(ProductoTalla::getStock)
                    .sum();
            stockTotales.put(producto.getId(), stockTotal);
        }
        
        model.addAttribute("productos", productos);
        model.addAttribute("stockTotales", stockTotales);
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String mostrarFormularioProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", obtenerCategorias());
        model.addAttribute("tallasDisponibles", productoService.obtenerTallasDisponibles());
        return "admin/form-producto";
    }

    @PostMapping("/productos")
    public String guardarProducto(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam String categoria,
            @RequestParam String imagenUrl,
            @RequestParam List<String> tallas,
            @RequestParam List<Integer> stocks) {
        
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setImagenUrl(imagenUrl);
        
        // Agregar tallas
        for (int i = 0; i < tallas.size(); i++) {
            if (stocks.get(i) > 0) { // Solo agregar tallas con stock > 0
                producto.agregarTalla(tallas.get(i), stocks.get(i));
            }
        }
        
        productoService.guardar(producto);
        return "redirect:/admin/productos?success=Producto creado correctamente";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", obtenerCategorias());
        model.addAttribute("tallasDisponibles", productoService.obtenerTallasDisponibles());
        return "admin/form-producto";
    }

    @GetMapping("/productos/acabar-stock/{id}")
    public String acabarStock(@PathVariable Long id) {
        if (productoService.existeProducto(id)) {
            Producto producto = productoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Poner todas las tallas a stock 0
            for (ProductoTalla talla : producto.getTallas()) {
                talla.setStock(0);
            }
            
            productoService.guardar(producto);
            return "redirect:/admin/productos?success=Stock del producto actualizado a 0";
        } else {
            return "redirect:/admin/productos?error=Producto no encontrado";
        }
    }
    
    private List<String> obtenerCategorias() {
        return List.of("Camisetas", "Pantalones", "Vestidos", "Chaquetas", "Zapatos", "Accesorios");
    }
}