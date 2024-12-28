package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.AuthReqDto;
import com.software.software_project_sem4.dto.UserReqDto;
import com.software.software_project_sem4.dto.StatusRespDto;
import com.software.software_project_sem4.dto.UserRespDto;
import com.software.software_project_sem4.exception.UnauthorizedException;
import com.software.software_project_sem4.exception.ValidationException;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.UserRepo;
import com.software.software_project_sem4.security.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRespDto login(AuthReqDto dto, HttpServletRequest request) {
        Optional<User> user = userRepo.findByEmail(dto.getEmail());
        boolean matched = false;

        if (user.isPresent()) {
            matched = this.passwordEncoder.bCrypt().matches(dto.getPassword(), user.get().getPassword());
        }

        if (!matched) {
            throw new UnauthorizedException("Invalid username or password");
        }

        HttpSession session = request.getSession();
        session.setAttribute("user_id", user.get().getId());

        UserRespDto userRespDto = new UserRespDto();
        userRespDto.setEmail(user.get().getEmail());
        userRespDto.setId(user.get().getId());
        userRespDto.setAvatar(user.get().getAvatar());
        userRespDto.setUserName(user.get().getUserName());
        return userRespDto;
    }

    public StatusRespDto signup(UserReqDto dto) {
        boolean userExisted = this.userRepo.existsByEmail(dto.getEmail()) || this.userRepo.existsByUserName(dto.getUserName());
        if (userExisted) {
            throw new ValidationException("User already exists");
        }
        String passwordHash = passwordEncoder.bCrypt().encode(dto.getPassword());
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUserName(dto.getUserName());
        user.setPassword(passwordHash);
        user.setAvatar(dto.getAvatar());

        this.userRepo.save(user);

        StatusRespDto statusRespDto = new StatusRespDto();
        statusRespDto.setSuccess(true);
        return statusRespDto;
    }

    public StatusRespDto logout(HttpServletRequest request) {

        HttpSession session = request.getSession();
        session.setAttribute("user_id", null);

        StatusRespDto statusRespDto = new StatusRespDto();
        statusRespDto.setSuccess(true);
        return statusRespDto;
    }
}
