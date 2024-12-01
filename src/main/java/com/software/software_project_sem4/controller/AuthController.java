package com.software.software_project_sem4.controller;

import com.software.software_project_sem4.aspect.AuthGuard;
import com.software.software_project_sem4.dto.AuthReqDto;
import com.software.software_project_sem4.dto.UserReqDto;
import com.software.software_project_sem4.dto.StatusRespDto;
import com.software.software_project_sem4.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    //end point for login(post): localhost:8080/api/v1/login
    //end point for signup(post): localhost:8080/api/v1/signup
    //end point for logout(post): localhost:8080/api/v1/logout
    @PostMapping("login")
    public StatusRespDto login(@Valid @RequestBody AuthReqDto dto, HttpServletRequest request) {
        return this.authService.login(dto, request);
    }

    @PostMapping("signup")
    public StatusRespDto signup(@Valid @RequestBody UserReqDto dto){
        return this.authService.signup(dto);
    }

    @PostMapping("logout")
    @AuthGuard
    public StatusRespDto logout(HttpServletRequest request){
        return this.authService.logout(request);
    }

}
