package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long>, CommentRepoCustom {
    // Fetch all comments for a specific post
    List<Comment> findByPostId(Long postId);
}
