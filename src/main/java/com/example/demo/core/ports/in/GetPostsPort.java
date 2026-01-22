package com.example.demo.core.ports.in;

import java.util.List;
import java.util.UUID;

import com.example.demo.core.domain.models.Post;
import com.example.demo.core.domain.models.TipoPost;

public interface GetPostsPort {
    List<Post> getAllPosts();
    Post getPostById(UUID id);
    List<Post> getPostsByTipo(TipoPost tipo);
    List<Post> getPostsByAutor(Long autorId);
}
