package com.notion.nsurfer.auth.util;

import com.notion.nsurfer.user.entity.User;

import java.time.LocalDate;
import java.util.UUID;

public class AuthRedisKeyUtils {
    public static final String DIVIDER = ":";
    public static String makeRedisAccessTokenKey(User user){
        return "email" + DIVIDER + user.getEmail() + DIVIDER +
                "provider" + DIVIDER + user.getProvider() + DIVIDER +
                "accessToken";
    }

    public static String makeRedisRefreshToken(User user){
        return "email" + DIVIDER + user.getEmail() + DIVIDER +
                "provider" + DIVIDER + user.getProvider() + DIVIDER +
                "refreshToken";
    }
}
