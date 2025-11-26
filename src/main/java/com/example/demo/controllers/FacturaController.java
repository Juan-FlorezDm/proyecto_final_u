package com.example.demo.controllers;

import com.example.demo.entidades.*;
import com.example.demo.servicios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/cliente/facturas")
public class FacturaController {

    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);
    
    @Autowired
    private FacturaService facturaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PDFGeneratorService pdfGeneratorService;
    
    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public String listarFacturas(Authentication authentication, Model model) {
        try {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            List<Pedido> pedidos = pedidoService.obtenerPedidosPorUsuario(usuario);
            
            model.addAttribute("pedidos", pedidos);
            return "cliente/facturas";
            
        } catch (Exception e) {
            logger.error("Error al listar facturas", e);
            model.addAttribute("error", "Error al cargar las facturas");
            return "cliente/facturas";
        }
    }
    
    @GetMapping("/{pedidoId}")
    public String verFactura(@PathVariable Long pedidoId, Authentication authentication, Model model) {
        try {
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            Factura factura = facturaService.obtenerFacturaPorPedido(pedidoId);
            
            // Verificar que la factura pertenece al usuario
            if (!factura.getPedido().getUsuario().getId().equals(usuario.getId())) {
                model.addAttribute("error", "No tienes permisos para ver esta factura");
                return "redirect:/cliente/facturas";
            }
            
            model.addAttribute("factura", factura);
            return "cliente/detalle-factura";
            
        } catch (Exception e) {
            logger.error("Error al ver factura: " + pedidoId, e);
            model.addAttribute("error", "Factura no encontrada");
            return "redirect:/cliente/facturas";
        }
    }
    
    @GetMapping("/{pedidoId}/descargar")
    public ResponseEntity<byte[]> descargarFacturaPDF(@PathVariable Long pedidoId, Authentication authentication) {
        try {
            logger.info("Solicitando descarga de factura para pedido: " + pedidoId);
            
            Usuario usuario = obtenerUsuarioDesdeAutenticacion(authentication);
            Factura factura = facturaService.obtenerFacturaPorPedido(pedidoId);
            
            // Verificar que la factura pertenece al usuario
            if (!factura.getPedido().getUsuario().getId().equals(usuario.getId())) {
                logger.warn("Intento de acceso no autorizado a factura: " + pedidoId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Generar PDF
            byte[] pdfBytes = pdfGeneratorService.generarFacturaPDF(factura);
            logger.info("PDF generado exitosamente, tamaño: " + pdfBytes.length + " bytes");
            
            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename("factura-" + factura.getNumeroFactura() + ".pdf")
                    .build());
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error al descargar factura: " + pedidoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al generar PDF: " + e.getMessage()).getBytes());
        }
    }
    
    private Usuario obtenerUsuarioDesdeAutenticacion(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email);
    }
}