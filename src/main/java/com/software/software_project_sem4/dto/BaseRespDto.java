package com.software.software_project_sem4.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
public abstract class BaseRespDto {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
