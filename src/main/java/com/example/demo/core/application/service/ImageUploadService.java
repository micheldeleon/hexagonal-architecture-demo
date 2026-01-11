package com.example.demo.core.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

import java.util.UUID;

/**
 * Servicio para subir imágenes a Supabase Storage
 */
@Service
public class ImageUploadService {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.storage.key}")
    private String supabaseStorageKey;
    
    private final WebClient webClient;
    
    public ImageUploadService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    
    /**
     * Sube una imagen de perfil de usuario a Supabase Storage
     * @param file Archivo de imagen
     * @return URL pública de la imagen
     */
    public String uploadUserImage(MultipartFile file) {
        return uploadImage(file, "profile-images");
    }
    
    /**
     * Sube una imagen de torneo a Supabase Storage
     * @param file Archivo de imagen
     * @return URL pública de la imagen
     */
    public String uploadTournamentImage(MultipartFile file) {
        return uploadImage(file, "tournament-images");
    }
    
    /**
     * Método genérico para subir imágenes a un bucket específico
     * @param file Archivo de imagen
     * @param bucket Nombre del bucket en Supabase
     * @return URL pública de la imagen
     */
    private String uploadImage(MultipartFile file, String bucket) {
        try {
            // Validar que el archivo es una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("El archivo debe ser una imagen");
            }
            
            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException("La imagen no debe superar 5MB");
            }
            
            // Generar nombre único para el archivo
            String extension = getFileExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            
            // Subir archivo a Supabase Storage
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;
            
            byte[] fileBytes = file.getBytes();
            
            webClient.post()
                .uri(uploadUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseStorageKey)
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header("x-upsert", "false")
                .body(BodyInserters.fromValue(fileBytes))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            
            // Retornar URL pública
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Elimina una imagen de Supabase Storage
     * @param imageUrl URL de la imagen a eliminar
     */
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains(supabaseUrl)) {
                return; // No es una imagen de nuestro storage
            }
            
            // Extraer bucket y fileName de la URL
            String[] parts = imageUrl.replace(supabaseUrl + "/storage/v1/object/public/", "").split("/", 2);
            if (parts.length < 2) {
                return;
            }
            
            String bucket = parts[0];
            String fileName = parts[1];
            
            String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;
            
            webClient.delete()
                .uri(deleteUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseStorageKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
                
        } catch (Exception e) {
            // Log error pero no lanzar excepción
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Extensión por defecto
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
