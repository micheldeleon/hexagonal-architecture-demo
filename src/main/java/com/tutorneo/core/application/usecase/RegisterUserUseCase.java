package com.tutorneo.core.application.usecase;

import org.springframework.transaction.annotation.Transactional;

import com.tutorneo.core.domain.models.NotificationType;
import com.tutorneo.core.domain.models.User;
import com.tutorneo.core.ports.in.CreateNotificationPort;
import com.tutorneo.core.ports.in.RegisterUserPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;

public class RegisterUserUseCase implements RegisterUserPort {
    private final UserRepositoryPort userRepository;
    private final CreateNotificationPort createNotificationPort;
    
    public RegisterUserUseCase(UserRepositoryPort userRepository, CreateNotificationPort createNotificationPort) {
        this.userRepository = userRepository;
        this.createNotificationPort = createNotificationPort;
    }

    @Override
    @Transactional
    public void registerUser(User user) {
        try{
            // Guardar el usuario
            userRepository.save(user);
            
            // Obtener el usuario guardado con su ID generado
            User savedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Error al recuperar usuario registrado"));
            
            // Crear notificación de bienvenida
            String welcomeTitle = "¡Bienvenido a Tutorneo!";
            String welcomeMessage = "¡Hola " + savedUser.getName() + "! 🎉\n\n" +
                "Te damos la bienvenida a nuestra plataforma de gestión de torneos. " +
                "Para comenzar a participar en torneos, es importante que completes todos los datos de tu perfil.\n\n" +
                "📋 Por favor, actualiza la siguiente información en tu perfil:\n" +
                "• Fecha de nacimiento\n" +
                "• Cédula de identidad\n" +
                "• Número de teléfono\n" +
                "• Dirección\n" +
                "Si deseas organizar tus propios torneos, puedes solicitar permisos de organizador desde tu perfil. " +
                "Una vez aprobada tu solicitud, podrás crear y gestionar torneos.\n\n" +
                "¡Nos alegra tenerte con nosotros!";
            
            createNotificationPort.createNotification(
                savedUser.getId(),
                NotificationType.WELCOME,
                welcomeTitle,
                welcomeMessage,
                null
            );
        } catch (Exception e) {
            String causeMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            if (causeMessage == null || causeMessage.isBlank()) {
                causeMessage = "Unknown error";
            }
            throw new RuntimeException("Failed to register user: " + causeMessage, e);
        }
    }
}
