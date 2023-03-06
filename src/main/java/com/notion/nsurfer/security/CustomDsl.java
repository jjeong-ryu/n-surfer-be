package com.notion.nsurfer.security;

import com.notion.nsurfer.security.filter.JwtAccessTokenFilter;
import com.notion.nsurfer.security.filter.JwtRefreshTokenFilter;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        JwtAccessTokenFilter jwtAccessTokenFilter = new JwtAccessTokenFilter(
                authenticationManager,
                userDetailsService,
                authenticationEntryPoint,
                userRepository,
                userLoginInfoRepository,
                redisTemplate);
        JwtRefreshTokenFilter jwtRefreshTokenFilter = new JwtRefreshTokenFilter(
                authenticationManager,
                userDetailsService,
                userLoginInfoRepository,
                userRepository);
        http.addFilterAt(jwtAccessTokenFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(jwtRefreshTokenFilter, BasicAuthenticationFilter.class);
    }
}
