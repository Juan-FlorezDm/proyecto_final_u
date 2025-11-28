package com.example.demo.controllers;

import com.example.demo.entidades.Producto;
import com.example.demo.entidades.ProductoTalla;
import com.example.demo.entidades.Usuario;
import com.example.demo.servicios.EmailService;
import com.example.demo.servicios.ProductoService;
import com.example.demo.servicios.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public String listarProductos(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String stock,
        Model model) {
    
    List<Producto> productos;
    
    // Aplicar filtro de categoría
    if (categoria != null && !categoria.isEmpty() && !categoria.equals("todas")) {
        productos = productoService.obtenerPorCategoria(categoria);
    } else {
        productos = productoService.obtenerTodos();
    }
    
    // Calcular el stock total para cada producto
    Map<Long, Integer> stockTotales = new HashMap<>();
    for (Producto producto : productos) {
        int stockTotal = producto.getTallas().stream()
                .mapToInt(ProductoTalla::getStock)
                .sum();
        stockTotales.put(producto.getId(), stockTotal);
    }
    
    // Aplicar filtro de stock si está presente
    if (stock != null && !stock.isEmpty()) {
        productos = filtrarPorStock(productos, stockTotales, stock);
    }
    
    // Obtener categorías únicas para el dropdown
    List<String> categorias = productoService.obtenerCategoriasUnicas();
    
    model.addAttribute("productos", productos);
    model.addAttribute("stockTotales", stockTotales);
    model.addAttribute("categorias", categorias);
    model.addAttribute("categoriaSeleccionada", categoria);
    model.addAttribute("stockSeleccionado", stock);
    
    return "admin/productos";
}

private List<Producto> filtrarPorStock(List<Producto> productos, Map<Long, Integer> stockTotales, String filtroStock) {
    return productos.stream()
            .filter(producto -> {
                int stockTotal = stockTotales.get(producto.getId());
                return switch (filtroStock.toLowerCase()) {
                    case "con-stock" -> stockTotal > 0;
                    case "sin-stock" -> stockTotal == 0;
                    case "stock-bajo" -> stockTotal > 0 && stockTotal <= 10; // Stock bajo: 10 o menos unidades
                    case "stock-alto" -> stockTotal > 10; // Stock alto: más de 10 unidades
                    default -> true; // "todos" o cualquier otro valor
                };
            })
            .collect(Collectors.toList());
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
        @RequestParam(required = false) Long id, // ✅ Agrega este parámetro
        @RequestParam String nombre,
        @RequestParam String descripcion,
        @RequestParam BigDecimal precio,
        @RequestParam String categoria,
        @RequestParam String imagenUrl,
        @RequestParam List<String> tallas,
        @RequestParam List<Integer> stocks,
        RedirectAttributes redirectAttributes) {
    
    try {
        Producto producto;
        
        if (id != null) {
            // ✅ EDITAR producto existente
            producto = productoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // Actualizar propiedades
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setImagenUrl(imagenUrl);
            
            // Limpiar tallas existentes y agregar las nuevas
            producto.getTallas().clear();
            
        } else {
            // ✅ CREAR nuevo producto
            producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setImagenUrl(imagenUrl);
        }
        
        // Agregar tallas (tanto para crear como editar)
        for (int i = 0; i < tallas.size(); i++) {
            if (stocks.get(i) > 0) {
                producto.agregarTalla(tallas.get(i), stocks.get(i));
            }
        }
        
        productoService.guardar(producto);
        
        String mensaje = (id != null) ? "Producto actualizado correctamente" : "Producto creado correctamente";
        redirectAttributes.addFlashAttribute("success", mensaje);
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al guardar producto: " + e.getMessage());
    }
    
    return "redirect:/admin/productos";
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

        @GetMapping("/productos/stock-cero/{id}")
    public String stockCero(@PathVariable Long id) {
        try {
            productoService.acabarStockProducto(id);
            return "redirect:/admin/productos?success=Todo el stock puesto a 0 correctamente";
        } catch (Exception e) {
            return "redirect:/admin/productos?error=Error: " + e.getMessage();
        }
    }

    @GetMapping("/productos/acabar-stock/{id}")
        public String acabarStock(@PathVariable Long id) {
            try {
                productoService.acabarStockProducto(id);
                return "redirect:/admin/productos?success=Stock del producto actualizado a 0";
            } catch (Exception e) {
                return "redirect:/admin/productos?error=Error al actualizar stock: " + e.getMessage();
            }
        }
    
    private List<String> obtenerCategorias() {
        return List.of("Camisetas", "Pantalones", "Vestidos", "Chaquetas", "Zapatos", "Accesorios");
    }

    @Autowired
    private UsuarioService usuarioService;
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        // Obtener todos los usuarios
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }
    @Autowired
    private EmailService emailService;

    @PostMapping("/usuarios/solicitar-cambio-password")
    public String solicitarCambioPassword(
            @RequestParam Long usuarioId,
            @RequestParam String usuarioEmail,
            @RequestParam String usuarioNombre,
            RedirectAttributes redirectAttributes) {
        
        try {
            // USAR EL SERVICIO REAL DE EMAIL
            emailService.enviarSolicitudCambioPassword(usuarioNombre, usuarioEmail, usuarioId);
            
            // También mostrar en consola para debugging
            System.out.println("=== EMAIL ENVIADO ===");
            System.out.println("Para: jnendez38@gmail.com");
            System.out.println("Usuario: " + usuarioNombre);
            System.out.println("Email: " + usuarioEmail);
            System.out.println("ID: " + usuarioId);
            System.out.println("======================");
            
            redirectAttributes.addFlashAttribute("success", 
                "✅ Solicitud de cambio de contraseña enviada para: " + usuarioNombre);
            
        } catch (Exception e) {
            try {
                // Mostrar error detallado en consola
                System.err.println("❌ ERROR ENVIANDO EMAIL: " + e.getMessage());
                e.printStackTrace();
                
                // Verificar si es un error de configuración
                if (e.getMessage().contains("configuration") || e.getMessage().contains("configuración")) {
                    System.err.println("⚠️  POSIBLE ERROR DE CONFIGURACIÓN EN RENDER");
                    System.err.println("Verifica las variables de entorno:");
                    System.err.println("SPRING_MAIL_HOST, SPRING_MAIL_USERNAME, SPRING_MAIL_PASSWORD");
                }
                
                redirectAttributes.addFlashAttribute("error", 
                    "❌ Error al enviar solicitud: " + e.getMessage());
                    
            } catch (Exception innerException) {
                // Catch interno por si falla algo dentro del catch principal
                System.err.println("❌ ERROR CRÍTICO EN EL MANEJO DE ERRORES: " + innerException.getMessage());
                redirectAttributes.addFlashAttribute("error", 
                    "❌ Error crítico en el sistema");
            }
        }
        
        return "redirect:/admin/usuarios";
    }
}