package com.software.software_project_sem4.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReqDto {

    @Email
    private String email;
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
}
