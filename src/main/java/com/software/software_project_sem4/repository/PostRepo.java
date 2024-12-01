package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepo extends JpaRepository<Post, Long>, PostRepoCustom {
}
