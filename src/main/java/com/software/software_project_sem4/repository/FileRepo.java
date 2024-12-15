package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface FileRepo extends JpaRepository<File, Long> {
     @Query("DELETE FROM File f WHERE f.id = :fileId AND f.post.user.id = :userId")
     int deleteByFileIdAndUserId(Long fileId, Long userId);

}
