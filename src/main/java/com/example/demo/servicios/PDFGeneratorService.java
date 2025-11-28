package com.example.demo.servicios;

import com.example.demo.entidades.Factura;
import com.example.demo.entidades.DetallePedido;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PDFGeneratorService {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

    public byte[] generarFacturaPDF(Factura factura) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            
            document.open();
            
            // Agregar contenido
            agregarEncabezado(document, factura);
            agregarInformacionEmpresaCliente(document, factura);
            agregarTablaItems(document, factura);
            agregarTotales(document, factura);
            agregarPiePagina(document);
            
            document.close();
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
    
    private void agregarEncabezado(Document document, Factura factura) throws DocumentException {
        // Título
        Paragraph title = new Paragraph("FACTURA DE VENTA", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Número de factura
        Paragraph invoiceNumber = new Paragraph("No: " + factura.getNumeroFactura(), BOLD_FONT);
        invoiceNumber.setAlignment(Element.ALIGN_RIGHT);
        invoiceNumber.setSpacingAfter(10);
        document.add(invoiceNumber);
        
        document.add(new Paragraph(" ")); // Espacio
    }
    
    private void agregarInformacionEmpresaCliente(Document document, Factura factura) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
    
        
        // Información del cliente
        PdfPCell clienteCell = new PdfPCell();
        clienteCell.setBorder(PdfPCell.NO_BORDER);
        clienteCell.addElement(new Paragraph("PARA:", SUBTITLE_FONT));
        clienteCell.addElement(new Paragraph(
            factura.getPedido().getUsuario().getNombre() + " " + 
            factura.getPedido().getUsuario().getApellido(), NORMAL_FONT));
        clienteCell.addElement(new Paragraph("Email: " + factura.getPedido().getUsuario().getEmail(), NORMAL_FONT));
        clienteCell.addElement(new Paragraph("Fecha: " + 
            factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), NORMAL_FONT));

        table.addCell(clienteCell);
        document.add(table);
        
        document.add(new Paragraph("Detalles del Pedido:", SUBTITLE_FONT));
        document.add(new Paragraph(" ")); // Espacio
    }
    
    private void agregarTablaItems(Document document, Factura factura) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20);
        
        // Configurar anchos de columnas
        float[] columnWidths = {3f, 1f, 1f, 1.5f, 1.5f};
        table.setWidths(columnWidths);
        
        // Encabezados
        String[] headers = {"Producto", "Talla", "Cantidad", "Precio Unit.", "Subtotal"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, BOLD_FONT));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Items
        for (DetallePedido item : factura.getItems()) {
            table.addCell(new Phrase(item.getProducto().getNombre(), NORMAL_FONT));
            table.addCell(new Phrase(item.getTalla(), NORMAL_FONT));
            table.addCell(new Phrase(item.getCantidad().toString(), NORMAL_FONT));
            table.addCell(new Phrase("$" + String.format("%,.2f", item.getPrecioUnitario()), NORMAL_FONT));
            table.addCell(new Phrase("$" + String.format("%,.2f", item.getSubtotal()), NORMAL_FONT));
        }
        
        document.add(table);
    }
    
    private void agregarTotales(Document document, Factura factura) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingAfter(20);
        
        table.addCell(crearCelda("Subtotal:", BOLD_FONT, Element.ALIGN_RIGHT));
        table.addCell(crearCelda("$" + String.format("%,.2f", factura.getSubtotal()), NORMAL_FONT, Element.ALIGN_RIGHT));
        
        table.addCell(crearCelda("Impuestos:", BOLD_FONT, Element.ALIGN_RIGHT));
        table.addCell(crearCelda("$" + String.format("%,.2f", factura.getImpuestos()), NORMAL_FONT, Element.ALIGN_RIGHT));
        
        table.addCell(crearCelda("TOTAL:", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD), Element.ALIGN_RIGHT));
        table.addCell(crearCelda("$" + String.format("%,.2f", factura.getTotal()), 
            new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD), Element.ALIGN_RIGHT));
        
        document.add(table);
    }
    
    private PdfPCell crearCelda(String texto, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        return cell;
    }
    
    private void agregarPiePagina(Document document) throws DocumentException {
        Paragraph gracias = new Paragraph("¡Gracias por su compra!", 
            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
        gracias.setAlignment(Element.ALIGN_CENTER);
        gracias.setSpacingBefore(20);
        document.add(gracias);
        
        Paragraph contacto = new Paragraph("Para consultas contacte a: servicioalcliente@tiendaropa.com", 
            new Font(Font.FontFamily.HELVETICA, 8));
        contacto.setAlignment(Element.ALIGN_CENTER);
        document.add(contacto);
    }
}