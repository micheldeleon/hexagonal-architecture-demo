package com.example.demo.core.ports.in;

import com.example.demo.core.domain.models.Post;

public interface CreatePostPort {
    Post createPost(Post post);
}
