package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Optional;

public class PostRepoCustomImpl implements PostRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int deleteByPostIdAndUserId(Long postId, Long userId) {
        Optional<Post> existingPost = findByPostIdAndUserId(postId, userId);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            entityManager.remove(post);
            return 1;
        }
        return 0;
    }

    @Override
    public Optional<Post> findByPostIdAndUserId(Long postId, Long userId) {
        String jpql = "SELECT p FROM Post p WHERE p.id = :postId AND p.user.id = :userId";
        TypedQuery<Post> query = entityManager.createQuery(jpql, Post.class);
        query.setParameter("postId", postId);
        query.setParameter("userId", userId);

        return query.getResultList().stream().findFirst();
    }
}
