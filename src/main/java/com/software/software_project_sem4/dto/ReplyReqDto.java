package com.software.software_project_sem4.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyReqDto {
    private String content;       // Content of the reply
    private Long parentReplyId;   // Optional, ID of the parent reply
}

