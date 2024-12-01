package com.software.software_project_sem4.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRespDto extends BaseRespDto {
    private String email;
    private String userName;
    private Long id;
}
