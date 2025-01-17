package com.software.software_project_sem4.dto;

import com.software.software_project_sem4.model.Base;
import com.software.software_project_sem4.model.User;
import lombok.Data;

import java.util.List;

@Data
public class ReplyRespDto extends BaseRespDto {
    private String replyContent;
    private Long id;
    private Long postId;
    private UserRespDto user;
    private List<User> likedByUsers;
    private boolean likedByCurrentUser;
}
