package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Post;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface PostRepoCustom {
    @Transactional
    int deleteByPostIdAndUserId(Long postId, Long userId);

    Optional<Post> findByPostIdAndUserId(Long postId, Long userId);
}
