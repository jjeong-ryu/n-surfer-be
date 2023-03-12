package com.notion.nsurfer.auth.controller;

import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses = AuthController.class)
public class AuthControllerAdvice {
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Object> cardNotFoundExceptionHandler(CardNotFoundException e){
        log.info(e.getMessage());
        return new ResponseEntity(ResponseCode.CARD_NOT_FOUND,HttpStatus.NOT_FOUND);
    }
}
