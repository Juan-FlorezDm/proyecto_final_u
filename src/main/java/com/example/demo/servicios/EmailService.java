package com.example.demo.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void enviarSolicitudCambioPassword(String usuarioNombre, String usuarioEmail, Long usuarioId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("jnendez38@gmail.com");
            message.setSubject("🔐 Solicitud de Cambio de Contraseña - " + usuarioNombre);
            message.setText(
                "Se ha solicitado un cambio de contraseña para el usuario:\n\n" +
                "🔸 Usuario: " + usuarioNombre + "\n" +
                "🔸 Email: " + usuarioEmail + "\n" +
                "🔸 ID: " + usuarioId + "\n\n" +
                "Por favor, proceda a cambiar la contraseña de este usuario en el sistema administrativo.\n\n" +
                "Fecha: " + java.time.LocalDateTime.now()
            );
            message.setFrom("no-reply@tiendaropa.com");
            
            mailSender.send(message);
            System.out.println("✅ Email enviado exitosamente a: jnendez38@gmail.com");
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando email: " + e.getMessage());
            throw new RuntimeException("Error enviando email", e);
        }
    }
}
