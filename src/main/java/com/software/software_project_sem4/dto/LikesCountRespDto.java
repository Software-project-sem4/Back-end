package com.software.software_project_sem4.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikesCountRespDto {
    private Boolean success;
    private Integer likesCount;
    private String message;
}
