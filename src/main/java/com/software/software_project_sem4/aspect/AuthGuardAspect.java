package com.software.software_project_sem4.aspect;

import com.software.software_project_sem4.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthGuardAspect {

    private final HttpSession session;

    @Autowired
    public AuthGuardAspect(HttpSession session) {
        this.session = session;
    }

    @Pointcut("@annotation(AuthGuard) || @within(AuthGuard)")
    public void authGuardAnnotated() {
    }

    @Before("authGuardAnnotated()")
    public void checkAuthentication(JoinPoint joinPoint) throws Throwable {
        if (session == null || session.getAttribute("user_id") == null) {
            throw new UnauthorizedException("Please login first");
        }
    }
}