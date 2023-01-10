package com.notion.nsuffer.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.nsuffer.user.repository.UserLoginInfoRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class JwtRefreshTokenFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDetailsService userDetailsService;
    private final UserLoginInfoRepository userLoginInfoRepository;

    public JwtRefreshTokenFilter(AuthenticationManager authenticationManager,
                                 UserDetailsService userDetailsService,
                                 UserLoginInfoRepository userLoginInfoRepository){
        super(new AntPathRequestMatcher("/v1/auth/reissue", "POST"), authenticationManager);
        this.userDetailsService = userDetailsService;
        this.userLoginInfoRepository = userLoginInfoRepository;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        /**
         * 추가 로직 작성 필요
         */
        return null;
    }
}
