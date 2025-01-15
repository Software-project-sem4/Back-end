package com.software.software_project_sem4.repository;

import com.software.software_project_sem4.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepo extends JpaRepository<Reply, Long> {
}
