package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Comment;
import com.software.software_project_sem4.model.Post;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface CommentRepoCustom {
    @Transactional
    int deleteByCommentIdAndUserIdRaw(Long commentId, Long userId);
    Optional<Comment> findByCommentIdAndUserId(Long commentId, Long userId);
}
