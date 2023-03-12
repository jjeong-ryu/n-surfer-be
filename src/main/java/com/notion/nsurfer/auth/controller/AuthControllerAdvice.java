package com.notion.nsurfer.auth.controller;

import com.notion.nsurfer.card.exception.CardNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthControllerAdvice {
    @ExceptionHandler(CardNotFoundException.class)
    public void cardNotFoundExceptionHandler(CardNotFoundException e){
    }
}
