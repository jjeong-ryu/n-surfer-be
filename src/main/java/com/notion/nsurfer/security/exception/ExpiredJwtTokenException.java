package com.notion.nsurfer.security.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredJwtTokenException extends AuthenticationException {
    public ExpiredJwtTokenException(String message){
        super(message);
    }
}
