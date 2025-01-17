package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class CommentRepoCustomImpl implements CommentRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int deleteByCommentIdAndUserIdRaw(Long commentId, Long userId) {
        Optional<Comment> comment = findByCommentIdAndUserId(commentId, userId);
        if (comment.isEmpty()) {
            return 0;
        }

        // Delete Comment
        String filesql = "DELETE FROM Comment f WHERE f.id = :commentId";
        Query fileQuery = entityManager.createQuery(filesql);
        fileQuery.setParameter("commentId", commentId);
        fileQuery.executeUpdate();
          return 1;
    }
    @Override
    public Optional<Comment> findByCommentIdAndUserId(Long commentId, Long userId) {
        String jpql = "SELECT p FROM Comment p WHERE p.id = :commentId AND p.user.id = :userId";
        TypedQuery<Comment> query = entityManager.createQuery(jpql, Comment.class);
        query.setParameter("commentId", commentId);
        query.setParameter("userId", userId);

        return query.getResultList().stream().findFirst();
    }
}
