package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.adapters.in.api.dto.ChangePasswordDto;
import com.example.demo.adapters.in.api.dto.UserFullDto;
import com.example.demo.adapters.in.api.dto.UserRegisterDto;
import com.example.demo.adapters.in.api.dto.UserResponseDTO;
import com.example.demo.adapters.in.api.mappers.UserMapperDtos;
import com.example.demo.core.application.service.ImageUploadService;
import com.example.demo.core.domain.models.Tournament;
import com.example.demo.core.domain.models.User;
import com.example.demo.core.ports.in.ChangePasswordPort;
import com.example.demo.core.ports.in.GetUserByIdPort;
import com.example.demo.core.ports.in.GetUserByIdAndEmailPort;
import com.example.demo.core.ports.in.ListUsersPort;
import com.example.demo.core.ports.in.RegisterUserPort;
import com.example.demo.core.ports.in.ToOrganizerPort;
import com.example.demo.core.ports.in.UpdateProfilePort;
import com.example.demo.core.ports.in.GetAllTournamentsPort;
import com.example.demo.core.ports.in.GetTournamentPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ListUsersPort listUsersPort;
    private final RegisterUserPort registerUserPort;
    private final UpdateProfilePort updateProfilePort;
    private final GetUserByIdAndEmailPort getUserByIdAndEmailPort;
    private final GetUserByIdPort getUserByIdPort;
    private final ToOrganizerPort toOrganizerPort;
    private final GetAllTournamentsPort getAllTournamentsPort;
    private final GetTournamentPort getTournamentPort;
    private final ImageUploadService imageUploadService;
    private final ChangePasswordPort changePasswordPort;
    
    public UserController(ListUsersPort listUsersPort, RegisterUserPort registerUserPort,
            UpdateProfilePort updateProfilePort, GetUserByIdAndEmailPort getUserPort, GetUserByIdPort getUserByIdPort,
            ToOrganizerPort toOrganizerPort, GetAllTournamentsPort getAllTournamentsPort, GetTournamentPort getTournamentPort,
            ImageUploadService imageUploadService, ChangePasswordPort changePasswordPort) {
        this.listUsersPort = listUsersPort;
        this.registerUserPort = registerUserPort;
        this.updateProfilePort = updateProfilePort;
        this.getUserByIdAndEmailPort = getUserPort;
        this.getUserByIdPort = getUserByIdPort;
        this.toOrganizerPort = toOrganizerPort;
        this.getAllTournamentsPort = getAllTournamentsPort;
        this.getTournamentPort = getTournamentPort;
        this.imageUploadService = imageUploadService;
        this.changePasswordPort = changePasswordPort;
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = listUsersPort.listUsers();
        return users.stream()
                .map(UserMapperDtos::toResponseDto)
                .collect(Collectors.toList());
    }// mover mapeo

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto entity) {
        try {
            registerUserPort.registerUser(UserMapperDtos.toDomain(entity));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/profile")
    public ResponseEntity<?> completeProfile(@Valid @RequestBody UserFullDto entity) {
        try {
            updateProfilePort.completion(UserMapperDtos.toDomain(entity));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping(params = {"id", "email"})
    public ResponseEntity<?> getUser(
            @RequestParam Long id,
            @RequestParam String email) {
        try {
            User user = getUserByIdAndEmailPort.getUserByIdAndEmail(id, email);
            return ResponseEntity.ok(UserMapperDtos.toFullDto(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(params = {"id"})
    public ResponseEntity<?> getUserById(
            @RequestParam Long id) {
        try {
            User user = getUserByIdPort.getUserById(id);
            return ResponseEntity.ok(UserMapperDtos.toFullDto(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

    @GetMapping(value = "/tournaments/organized", params = {"id", "email"})
    public ResponseEntity<?> getTournamentsOrganizedByUserIdandEmail(
            @RequestParam Long id,
            @RequestParam String email) {
        try {
            // Verificar que el usuario existe
            User user = getUserByIdAndEmailPort.getUserByIdAndEmail(id, email);
            
            // Obtener todos los torneos y filtrar por organizador
            List<Tournament> allTournaments = getAllTournamentsPort.getAllTournaments();
            List<Tournament> userTournaments = allTournaments.stream()
                    .filter(tournament -> tournament.getOrganizer() != null && 
                            tournament.getOrganizer().getId() != null &&
                            tournament.getOrganizer().getId().equals(user.getId()))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(userTournaments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value = "/tournaments", params = {"id", "email"})
    public ResponseEntity<?> getTournamentsByUserIdandEmail(
            @RequestParam Long id,
            @RequestParam String email) {
        try {
            // Verificar que el usuario existe
            User user = getUserByIdAndEmailPort.getUserByIdAndEmail(id, email);
            
            // Obtener torneos donde el usuario está inscrito como participante
            List<Tournament> subscribedTournaments = getTournamentPort.getSubscribedTournaments(user.getNationalId());
            
            return ResponseEntity.ok(subscribedTournaments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
     @PostMapping(value = "/organizer")
    public ResponseEntity<?> toOrganizer(
            @RequestParam Long id) {
        try {
            toOrganizerPort.toOrganizer(id);
            return ResponseEntity.ok("Exito");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Endpoint para subir imagen de perfil de usuario
     * POST /api/users/{id}/profile-image
     * Solo el propio usuario puede subir/cambiar su imagen de perfil
     */
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<?> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile file,
            org.springframework.security.core.Authentication authentication) {
        try {
            // Validar autenticación
            if (authentication == null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body("Debe estar autenticado para subir imágenes");
            }
            
            String userEmail = authentication.getName();
            
            // Obtener usuario
            User user = getUserByIdPort.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
            }
            
            // Validar que el usuario autenticado sea el mismo que está actualizando
            if (!user.getEmail().equals(userEmail)) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("Solo puedes actualizar tu propia imagen de perfil");
            }
            
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No se proporcionó archivo");
            }
            
            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("El archivo debe ser una imagen");
            }
            
            // Subir imagen a Supabase Storage
            String imageUrl = imageUploadService.uploadUserImage(file);
            
            // Actualizar usuario con la nueva URL
            user.setProfileImageUrl(imageUrl);
            updateProfilePort.completion(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "Imagen subida exitosamente",
                "imageUrl", imageUrl
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Endpoint para cambiar contraseña de usuario
     * PUT /api/users/{id}/change-password
     * Solo el propio usuario puede cambiar su contraseña
     */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto changePasswordDto,
            org.springframework.security.core.Authentication authentication) {
        try {
            // Validar autenticación
            if (authentication == null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body("Debe estar autenticado para cambiar la contraseña");
            }
            
            String userEmail = authentication.getName();
            
            // Obtener usuario
            User user = getUserByIdPort.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
            }
            
            // Validar que el usuario autenticado sea el mismo que está cambiando la contraseña
            if (!user.getEmail().equals(userEmail)) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("Solo puedes cambiar tu propia contraseña");
            }
            
            // Cambiar contraseña
            changePasswordPort.changePassword(id, changePasswordDto.getCurrentPassword(), changePasswordDto.getNewPassword());
            
            return ResponseEntity.ok(Map.of("message", "Contraseña cambiada exitosamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cambiar la contraseña: " + e.getMessage());
        }
    }
    // @GetMapping(params = {"id", "email"})
    // public ResponseEntity<?> getTournamentsByUserIdandEmail(
    //         @RequestParam Long id,
    //         @RequestParam String Email) {
    //     try {
    //         List<Tournament> tournaments = this.getUserPort.getUserByIdAndEmail(id, Email).getTournaments();
    //         return ResponseEntity.ok(tournaments);
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body(e.getMessage());
    //     }
    // }
}
