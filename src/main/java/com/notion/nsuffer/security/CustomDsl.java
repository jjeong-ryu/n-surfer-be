package com.notion.nsuffer.security;

import com.notion.nsuffer.security.filter.JwtAccessTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        JwtAccessTokenFilter jwtAccessTokenFilter
                = new JwtAccessTokenFilter(authenticationManager,userDetailsService, authenticationEntryPoint);
        http.addFilterAt(jwtAccessTokenFilter, BasicAuthenticationFilter.class);
    }
}
