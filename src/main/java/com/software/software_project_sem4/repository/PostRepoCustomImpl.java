package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.File;
import com.software.software_project_sem4.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
    public int deleteByPostIdAndUserIdRaw(Long postId, Long userId) {
//        String[] rawsql = {
//                "DELETE FROM File f Where f.postId=:postId AND f.userId=:userId",
//                "DELETE FROM users_liked_posts WHERE users_liked_posts.postId=:postId",
//                "DELETE FROM users_saved_posts WHERE users_saved_posts.postId=:postId",
//                "DELETE FROM Post p WHERE p.id =:postId AND p.user.id = :userId",
//        };
//        String joinRawSql = String.join(";", rawsql);
//        TypedQuery<Post> query = entityManager.createQuery(joinRawSql, Post.class);
//        query.setParameter("postId", postId);
//        query.setParameter("userId", userId);
//
//        query.executeUpdate();
//
//        return query.getResultList().size();
        Optional<Post> post = findByPostIdAndUserId(postId, userId);
        if (post.isEmpty()) {
            return 0;
        }

        // Delete files related to the post
        String filesql = "DELETE FROM File f WHERE f.post.id = :postId";
        Query fileQuery = entityManager.createQuery(filesql);
        fileQuery.setParameter("postId", postId);
        fileQuery.executeUpdate();

        // Delete likes related to the post
        String likesql = "DELETE FROM users_liked_posts WHERE post_id = :postId";
        Query likeQuery = entityManager.createNativeQuery(likesql);
        likeQuery.setParameter("postId", postId);
        likeQuery.executeUpdate();

        // Delete saved posts related to the post
        String savesql = "DELETE FROM users_saved_posts WHERE post_id = :postId";
        Query saveQuery = entityManager.createNativeQuery(savesql);
        saveQuery.setParameter("postId", postId);
        saveQuery.executeUpdate();

        // Finally, delete the post
        String postsql = "DELETE FROM post WHERE id = :postId AND user_id = :userId";
        Query postQuery = entityManager.createNativeQuery(postsql);
        postQuery.setParameter("postId", postId);
        postQuery.setParameter("userId", userId);
        postQuery.executeUpdate();

        return 1;
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
