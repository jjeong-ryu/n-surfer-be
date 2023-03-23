package com.notion.nsurfer.security.config;

import com.notion.nsurfer.security.CustomDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomDsl customDsl;
    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests().and()
//                .requestMatchers(
//                        "/auth/reissue/access-token",
//                        "/auth/reissue/access-refresh-token",
//                        "/user/**",
//                        "/auth/login/**",
//                        "/actuator/**")
//                .permitAll().and()
                .cors().and()
                .apply(customDsl)
                .and()
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers("/auth/reissue/access-token")
                    .requestMatchers("/auth/reissue/access-refresh-token")
                    .requestMatchers("/user/**")
                    .requestMatchers("/auth/login/**")
                    .requestMatchers("/actuator/**")
                    .requestMatchers(HttpMethod.GET,"/card")
                    .requestMatchers(HttpMethod.GET,"/card/**")
                    .requestMatchers("/auth/test");
        };
    }
}
