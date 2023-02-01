package com.notion.nsurfer.user.service;

import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String emailAndProvider) throws UsernameNotFoundException {
        String email = emailAndProvider.split("_")[0];
        String provider = emailAndProvider.split("_")[1];
        User user = userRepository.findByEmailAndProvider(email, provider)
                .orElseThrow(()->new UsernameNotFoundException("존재하지 않는 유저입니다."));
        return user;
    }
}
