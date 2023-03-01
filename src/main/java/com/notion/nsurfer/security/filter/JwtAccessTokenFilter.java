package com.notion.nsurfer.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.security.VerifyResult;
import com.notion.nsurfer.security.exception.ExpiredJwtTokenException;
import com.notion.nsurfer.security.exception.InvalidJwtException;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class JwtAccessTokenFilter extends BasicAuthenticationFilter {
    private UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserRepository userRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, String> redisTemplate;
    public JwtAccessTokenFilter(AuthenticationManager authenticationManager,
                                UserDetailsService userDetailsService,
                                AuthenticationEntryPoint authenticationEntryPoint,
                                UserRepository userRepository,
                                UserLoginInfoRepository userLoginInfoRepository,
                                RedisTemplate<String, String> redisTemplate
    ){
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userRepository = userRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
        this.redisTemplate = redisTemplate;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        VerifyResult verifyAccessTokenResult = null;
        try {
            verifyAccessTokenResult = verifyAccessToken(request);
        } catch (ExpiredJwtTokenException expiredAccessTokenException) {
            makeExpiredAccessTokenResponse(response);
            return;
        } catch (InvalidJwtException invalidAccessTokenException) {
            logger.info(invalidAccessTokenException.getMessage());
            makeInvalidAccessTokenResponse(response);
        }
        String emailAndProvider = verifyAccessTokenResult.getEmailAndProvider();
        User user = (User) this.userDetailsService.loadUserByUsername(emailAndProvider);
            // jwt가 유효하다는 것은 이미 인증이 되었다는 뜻이므로 authenticated로 넘긴다
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.authenticated(user, null,
                user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);

        chain.doFilter(request, response);
    }

    private VerifyResult verifyAccessToken(HttpServletRequest request){
        String accessToken = JwtUtil.resolveToken(request);
//        return JwtUtil.validateTokenWithRedis(accessToken);
        return JwtUtil.validateToken(accessToken);
    }

    private void makeExpiredAccessTokenResponse(HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .responseCode(ResponseCode.ERROR_EXPIRED_ACCESS_TOKEN)
                .data(null)
                .build();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(responseDto));
    }

    private String makeNewAccessToken(HttpServletRequest request){
        String emailAndProvider = JwtUtil.extractSubjectFromRequest(request);
        String email = getEmailFromEmailAndProvider(emailAndProvider);
        String provider = getProviderFromEmailAndProvider(emailAndProvider);
        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(UserNotFoundException::new);
        return JwtUtil.createAccessToken(user);
    }

    private void makeInvalidAccessTokenResponse(HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .responseCode(ResponseCode.ERROR_INVALID_ACCESS_TOKEN)
                .data(null)
                .build();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(responseDto));
    }

    private String getEmailFromEmailAndProvider(String emailAndProvider){
        return emailAndProvider.split("_")[0];
    }
    private String getProviderFromEmailAndProvider(String emailAndProvider){
        return emailAndProvider.split("_")[0];
    }
}
