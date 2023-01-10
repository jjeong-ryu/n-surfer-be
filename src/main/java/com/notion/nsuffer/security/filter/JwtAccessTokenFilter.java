package com.notion.nsuffer.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAccessTokenFilter extends BasicAuthenticationFilter {
    private UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    public JwtAccessTokenFilter(AuthenticationManager authenticationManager,
                                UserDetailsService userDetailsService,
                                AuthenticationEntryPoint authenticationEntryPoint
    ){
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilterInternal(request, response, chain);
    }
}
