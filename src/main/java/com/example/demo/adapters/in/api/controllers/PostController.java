package com.example.demo.adapters.in.api.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adapters.in.api.dto.CreatePostDto;
import com.example.demo.adapters.in.api.dto.PostDto;
import com.example.demo.adapters.in.api.mappers.PostMapperDtos;
import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;
import com.example.demo.core.ports.in.ClosePostPort;
import com.example.demo.core.ports.in.CreatePostPort;
import com.example.demo.core.ports.in.GetPostsPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
@Validated
public class PostController {
    
    private final CreatePostPort createPostPort;
    private final GetPostsPort getPostsPort;
    private final ClosePostPort closePostPort;
    
    public PostController(CreatePostPort createPostPort, 
                         GetPostsPort getPostsPort,
                         ClosePostPort closePostPort) {
        this.createPostPort = createPostPort;
        this.getPostsPort = getPostsPort;
        this.closePostPort = closePostPort;
    }
    
    /**
     * Crear un nuevo post
     * POST /api/posts
     */
    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody CreatePostDto dto) {
        try {
            Post post = PostMapperDtos.toDomain(dto);
            Post created = createPostPort.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(PostMapperDtos.toDto(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtener todos los posts (ordenados por fecha descendente)
     * GET /api/posts
     */
    @GetMapping
    public ResponseEntity<List<PostDto>> getAllPosts() {
        List<Post> posts = getPostsPort.getAllPosts();
        List<PostDto> dtos = posts.stream()
            .map(PostMapperDtos::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Obtener un post por ID
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID id) {
        try {
            Post post = getPostsPort.getPostById(id);
            return ResponseEntity.ok(PostMapperDtos.toDto(post));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Obtener posts por tipo
     * GET /api/posts/tipo/{tipo}
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PostDto>> getPostsByTipo(@PathVariable String tipo) {
        try {
            TipoPost tipoPost = TipoPost.valueOf(tipo);
            List<Post> posts = getPostsPort.getPostsByTipo(tipoPost);
            List<PostDto> dtos = posts.stream()
                .map(PostMapperDtos::toDto)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtener posts por autor
     * GET /api/posts/autor/{autorId}
     */
    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<PostDto>> getPostsByAutor(@PathVariable Long autorId) {
        List<Post> posts = getPostsPort.getPostsByAutor(autorId);
        List<PostDto> dtos = posts.stream()
            .map(PostMapperDtos::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    /**
     * Cerrar un post (solo el autor puede hacerlo)
     * PUT /api/posts/{id}/cerrar
     */
    @PutMapping("/{id}/cerrar")
    public ResponseEntity<Void> closePost(@PathVariable UUID id, @RequestParam Long userId) {
        try {
            closePostPort.closePost(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
