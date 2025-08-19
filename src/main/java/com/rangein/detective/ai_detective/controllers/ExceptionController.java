package com.rangein.detective.ai_detective.controllers;

import com.rangein.detective.ai_detective.exceptions.BaseException;
import com.rangein.detective.ai_detective.exceptions.auth.NoUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<String> handleNoUserException(BaseException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getMessage());
    }



}
