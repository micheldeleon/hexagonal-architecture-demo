package com.example.demo.core.ports.in;

import java.util.UUID;

public interface ClosePostPort {
    void closePost(UUID postId, Long userId);
}
