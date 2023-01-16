package com.notion.nsuffer.security.filter;

import com.notion.nsuffer.security.VerifyResult;
import com.notion.nsuffer.security.util.JwtUtil;
import com.notion.nsuffer.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            String jwt = JwtUtil.resolveToken(request);
            VerifyResult verifiedResult = JwtUtil.validateToken(jwt);

            String userName = verifiedResult.getUsername();
            User user = (User) this.userDetailsService.loadUserByUsername(userName);

            // jwt가 유효하다는 것은 이미 인증이 되었다는 뜻이므로 authenticated로 넘긴다
            UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(user, null,
                    user.getAuthorities());

            // SecurityContextHolder.getContext().setAuthentication(token);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(token);
            SecurityContextHolder.setContext(context);
        } catch (AuthenticationException e){
            SecurityContextHolder.clearContext();
            this.authenticationEntryPoint.commence(request, response, e);
            return;
        }

        chain.doFilter(request, response);
    }
}
