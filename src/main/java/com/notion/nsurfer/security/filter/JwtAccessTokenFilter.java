package com.notion.nsurfer.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.security.VerifyResult;
import com.notion.nsurfer.security.dto.ExpiredAccessTokenDto;
import com.notion.nsurfer.security.exception.ExpiredJwtTokenException;
import com.notion.nsurfer.security.exception.InvalidJwtException;
import com.notion.nsurfer.security.exception.JwtExceptionMessage;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final UserRepository userRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public JwtAccessTokenFilter(AuthenticationManager authenticationManager,
                                UserDetailsService userDetailsService,
                                AuthenticationEntryPoint authenticationEntryPoint,
                                UserRepository userRepository,
                                UserLoginInfoRepository userLoginInfoRepository
    ){
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userRepository = userRepository;
        this.userLoginInfoRepository = userLoginInfoRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        VerifyResult verifyAccessTokenResult = null;
        try {
            verifyAccessTokenResult = verifyAccessToken(request);
        } catch (ExpiredJwtTokenException expiredAccessTokenException) {
            try {
                verifyRefreshToken(request);
                makeExpiredAccessTokenResponse(request, response);
            } catch (ExpiredJwtTokenException expiredRefreshTokenException) {
                logger.info(expiredRefreshTokenException.getMessage());
                makeExpiredRefreshTokenResponse(request, response);
            } catch (InvalidJwtException invalidRefreshTokenException){
                logger.info(invalidRefreshTokenException.getMessage());
                makeInvalidRefreshTokenResponse(response);
            } finally {
                return;
            }

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
//        } catch (AuthenticationException e){
//            SecurityContextHolder.clearContext();
//            return;
//        }

        chain.doFilter(request, response);
    }

    private VerifyResult verifyAccessToken(HttpServletRequest request){
        String accessToken = JwtUtil.resolveToken(request);
        return JwtUtil.validateToken(accessToken);
    }

    private VerifyResult verifyRefreshToken(HttpServletRequest request){
        String refreshToken = JwtUtil.resolveToken(request);
        return JwtUtil.validateToken(refreshToken);
    }

    private void makeExpiredAccessTokenResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String newAccessToken = makeNewAccessToken(request);
        // 새로운 토큰으로 최신화(Mysql, redis)
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<ExpiredAccessTokenDto.Response> responseDto = ResponseDto.<ExpiredAccessTokenDto.Response>builder()
                .responseCode(ResponseCode.ERROR_EXPIRED_ACCESS_TOKEN)
                .data(ExpiredAccessTokenDto.Response.builder()
                        .newAccessToken(newAccessToken).build())
                .build();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(responseDto));
    }

    private String makeNewAccessToken(HttpServletRequest request){
        String emailAndProvider = JwtUtil.extractSubjectFromRequest(request);
        String email = emailAndProvider.split("_")[0];
        String provider = emailAndProvider.split("_")[1];
        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(UserNotFoundException::new);
        return JwtUtil.createAccessToken(user);
    }

    private void makeExpiredRefreshTokenResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String emailAndProvider = JwtUtil.extractSubjectFromRequest(request);
        String email = getEmailFromEmailAndProvider(emailAndProvider);
        String provider = getProviderFromEmailAndProvider(emailAndProvider);
        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(UserNotFoundException::new);
        // 새로운 토큰으로 최신화(Mysql, redis)
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .responseCode(ResponseCode.ERROR_EXPIRED_ACCESS_TOKEN)
                .data(null)
                .build();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(responseDto));
    }

    private void makeInvalidAccessTokenResponse(HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .responseCode(ResponseCode.ERROR_INVALID_ACCESS_TOKEN)
                .data(null)
                .build();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(responseDto));
    }
    private void makeInvalidRefreshTokenResponse(HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseDto<Object> responseDto = ResponseDto.builder()
                .responseCode(ResponseCode.ERROR_INVALID_REFRESH_TOKEN)
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
