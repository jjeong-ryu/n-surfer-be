package com.notion.nsurfer.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.security.VerifyResult;
import com.notion.nsurfer.security.exception.JwtExceptionMessage;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.UserLoginInfo;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static com.notion.nsurfer.security.exception.JwtExceptionMessage.*;

public class JwtRefreshTokenFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    public JwtRefreshTokenFilter(AuthenticationManager authenticationManager,
                                 UserDetailsService userDetailsService,
                                 UserLoginInfoRepository userLoginInfoRepository,
                                 UserRepository userRepository){
        super(new AntPathRequestMatcher("/auth/reissue", "POST"), authenticationManager);
        this.userDetailsService = userDetailsService;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String jwt = JwtUtil.resolveToken(request);
        VerifyResult verifiedResult = JwtUtil.validateToken(jwt);

        String emailAndProvider = verifiedResult.getEmailAndProvider();
        User user = (User) this.userDetailsService.loadUserByUsername(emailAndProvider);

        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(user, emailAndProvider,
                user.getAuthorities());

        return token;
    }

    /**
     * 토큰 검증 성공 시, 새로운 액세스토큰을 만들고 redis에 저장 및 DB 최신화
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication token) throws IOException, ServletException {
//        String[] emailAndProvider = ((String)token.getCredentials()).split("_");
//        String userEmail = null;
//        String userProvider = null;
//        try{
//            userEmail = emailAndProvider[0];
//            userProvider = emailAndProvider[1];
//        } catch (IndexOutOfBoundsException e){
//            logger.error("email과 provider가 정상적으로 파싱되지 않았습니다.");
//            return;
//        }
//        User user = userRepository.findByEmailAndProvider(userEmail, userProvider)
//                .orElse(null);
//        UserLoginInfo userLoginInfo = userLoginInfoRepository.findByEmailAAndProvider((String) token.getPrincipal())
//                .orElse(null);
//        if(user == null|| userLoginInfo == null){
//            return;
//        }
//        //
//        String newRefreshToken = JwtUtil.createRefreshToken(user);
//
//        //
//        SecurityContext context = SecurityContextHolder.createEmptyContext();
//        context.setAuthentication(token);
//        SecurityContextHolder.setContext(context);
//        doFilter(request,response,chain);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if(failed.getMessage().equals(EXPIRED_JWT_EXCEPTION)){

        }
    }
}
