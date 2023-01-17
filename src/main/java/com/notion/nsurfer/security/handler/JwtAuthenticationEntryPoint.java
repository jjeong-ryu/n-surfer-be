package com.notion.nsurfer.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private ResponseDto<String> responseDto = new ResponseDto<>(ResponseCode.ERROR_UNAUTHENTICATED, null);
    private ObjectMapper mapper = new ObjectMapper();
    private final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8"; // should be deprecated

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.responseDto.setData(authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_UTF8_VALUE);
        response.getWriter().write(this.mapper.writeValueAsString(this.responseDto));
    }
}
