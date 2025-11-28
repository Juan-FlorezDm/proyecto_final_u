// package com.example.demo.config;

// import com.example.demo.entidades.Usuario;
// import com.example.demo.entidades.Rol; // Asegúrate de importar el enum Rol
// import com.example.demo.repository.UsuarioRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// @Component
// public class AdminInitializer implements CommandLineRunner {

//     @Autowired
//     private UsuarioRepository usuarioRepository;
    
//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) throws Exception {
//         crearUsuarioAdmin();
//     }
    
//     private void crearUsuarioAdmin() {
//         // Verificar si ya existe un usuario admin
//         if (usuarioRepository.findByEmail("admin@gmail.com").isEmpty()) {
//             Usuario admin = new Usuario();
//             admin.setNombre("Admin");
//             admin.setApellido("Sistema");
//             admin.setEmail("admin@gmail.com");
//             admin.setPassword(passwordEncoder.encode("JUAn27022*"));
//             admin.setRol(Rol.ADMIN); // Usando el enum Rol
            
//             usuarioRepository.save(admin);
//             System.out.println("✅ Usuario admin creado exitosamente: admin@gmail.com");
//         } else {
//             System.out.println("ℹ️ Usuario admin ya existe en el sistema");
//         }
//     }
// }
