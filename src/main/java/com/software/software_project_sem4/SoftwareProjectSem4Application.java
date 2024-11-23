package com.software.software_project_sem4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@RestController
public class SoftwareProjectSem4Application {
	public static void main(String[] args) {
		SpringApplication.run(SoftwareProjectSem4Application.class, args);
	}

	@GetMapping
	public String hello(){
	return	"hello world";
	}

}





























































































































