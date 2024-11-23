package com.software.software_project_sem4.service;

import com.software.software_project_sem4.dto.AuthReqDto;
import com.software.software_project_sem4.dto.UserReqDto;
import com.software.software_project_sem4.dto.AuthRespDto;
import com.software.software_project_sem4.exception.UnauthorizedException;
import com.software.software_project_sem4.exception.ValidationException;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.UserRepo;
import com.software.software_project_sem4.security.PasswordEncoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public AuthRespDto login(AuthReqDto dto, HttpServletRequest request) {
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

        AuthRespDto authRespDto = new AuthRespDto();
        authRespDto.setSuccess(true);
        return authRespDto;
    }

    public AuthRespDto signup(UserReqDto dto) {
       Boolean userExisted =  this.userRepo.existsByEmail(dto.getEmail()) || this.userRepo.existsByUserName(dto.getUserName());
       if(userExisted) {
           throw new ValidationException("User already exists");
       }
        String passwordHash = passwordEncoder.bCrypt().encode(dto.getPassword());
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserReqDto, User>() {
            @Override
            protected void configure() {
                map().setPassword(passwordHash);
            }
        });

//       1: code chay
//       User user = new User();
//       user.setEmail(dto.getEmail());
//       user.setUserName(dto.getUserName());
//       user.setPassword(passwordHash);


//      2: Mappers
        User user = modelMapper.map(dto, User.class);
       this.userRepo.save(user);

        AuthRespDto authRespDto = new AuthRespDto();
        authRespDto.setSuccess(true);
        return authRespDto;
    }

    public AuthRespDto logout(HttpServletRequest request) {

        HttpSession session = request.getSession();
        session.setAttribute("user_id", null);

        AuthRespDto authRespDto = new AuthRespDto();
        authRespDto.setSuccess(true);
        return authRespDto;
    }
}
