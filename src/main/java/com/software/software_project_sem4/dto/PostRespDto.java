package com.software.software_project_sem4.dto;

import com.software.software_project_sem4.model.File;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRespDto extends BaseRespDto {
    private String content;
    private UserRespDto user;
    private Long id;
    private Set<File> files = new HashSet<>();
    private String avatar;
}
