//package com.notion.nsuffer.common.config.security.filter;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.notion.nsuffer.user.dto.UserLoginRequestDto;
//import com.notion.nsuffer.user.entity.User;
//import com.notion.nsuffer.user.entity.UserLoginInfo;
//import com.notion.nsuffer.user.exception.EmailNotFoundException;
//import com.notion.nsuffer.user.repository.UserLoginInfoRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import java.io.IOException;
//
//
//public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {
//    private ObjectMapper objectMapper = new ObjectMapper();
//    private final UserLoginInfoRepository userLoginInfoRepository;
//
//    public JwtLoginFilter(AuthenticationManager authenticationManager,
//                          UserLoginInfoRepository userLoginInfoRepository){
//        super(new AntPathRequestMatcher("/v1/auth/login", "POST"), authenticationManager);
//        this.userLoginInfoRepository = userLoginInfoRepository;
//    }
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//        UserLoginRequestDto userLoginRequestDto;
//        userLoginRequestDto = objectMapper.readValue(request.getInputStream(), UserLoginRequestDto.class);
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                userLoginRequestDto.getEmail(),
//                userLoginRequestDto.getPassword());
//        return getAuthenticationManager().authenticate(token);
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        User user = (User) authResult.getPrincipal();
//        UserLoginInfo userLoginInfo = this.getUserLoginInfo(user.getEmail(), user);
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//        super.unsuccessfulAuthentication(request, response, failed);
//    }
//
//    public UserLoginInfo getUserLoginInfo(String email, User user){
//        UserLoginInfo userLoginInfo = userLoginInfoRepository.findByEmail(email).orElse(null);
//        if(userLoginInfo == null){
//            UserLoginInfo.builder().email(email)
//                    .accessToken(JwtUtil.createAccessToken(user))
//                    .build();
//        }
//        userLoginInfoRepository.save()
//    }
//}
