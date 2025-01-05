package com.software.software_project_sem4;

import com.software.software_project_sem4.dto.StatusRespDto;
import com.software.software_project_sem4.exception.ValidationException;
import com.software.software_project_sem4.model.User;
import com.software.software_project_sem4.repository.FileRepo;
import com.software.software_project_sem4.repository.PostRepo;
import com.software.software_project_sem4.repository.UserRepo;
import com.software.software_project_sem4.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Value("${DataHibernate}")
    private String databaseHibernate;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final FileRepo fileRepo;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserRepo userRepo, PostRepo postRepo, FileRepo fileRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.fileRepo = fileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        System.out.println("Database initialization");
        if(userRepo.count() > 0) {
            return;
        }

        String passwordHash = passwordEncoder.bCrypt().encode(databaseHibernate);
        User user = new User();
        user.setEmail("huyhothien@gmail.com");
        user.setUserName("thien");
        user.setPassword(passwordHash);
        user.setAvatar("issac newton");

        this.userRepo.save(user);


    }
}
