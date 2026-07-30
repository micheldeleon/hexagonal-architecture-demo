package com.tutorneo.core.ports.in;

import com.tutorneo.core.domain.models.Post;

public interface CreatePostPort {
    Post createPost(Post post);
}
