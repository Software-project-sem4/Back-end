package com.software.software_project_sem4.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentRespDto {
    private Long id;
    private UserRespDto user;
    private String commentContent;
    private int likesCount;
    private int repliesCount;
    private String createdAt;
    private String updatedAt;
    private boolean likedByCurrentUser;
    // Nested replies if needed
    private List<CommentRespDto> replies;
}
