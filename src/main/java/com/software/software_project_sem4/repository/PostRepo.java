package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface PostRepo extends JpaRepository<Post, Long>, PostRepoCustom {
//    @Modifying
//    @Query(value = "delete from Post p where p.id=:postId AND p.user.id =:userId")
//    int deleteByPostIdAndUserIdRaw(Long postId, Long userId);
}
