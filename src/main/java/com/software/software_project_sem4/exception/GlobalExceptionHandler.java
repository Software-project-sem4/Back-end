package com.software.software_project_sem4.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CustomErrResp> handleAuthErrors(ValidationException ex){
        Map<String, List<String>> errors = Map.of("message", List.of(ex.getMessage()));

        CustomErrResp errorResponse = new CustomErrResp();
        errorResponse.setErrors(errors);

        return new ResponseEntity<>(errorResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
