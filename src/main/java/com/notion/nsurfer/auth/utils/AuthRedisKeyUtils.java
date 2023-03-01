package com.notion.nsurfer.auth.utils;

import com.notion.nsurfer.user.entity.User;

public class AuthRedisKeyUtils {
    public static final String DIVIDER = ":";
    public static String makeRedisAccessTokenKey(User user){
        return "email" + DIVIDER + user.getEmail() +
                "provider" + DIVIDER + user.getProvider() +
                "accessToken";
    }

    public static String makeRedisRefreshToken(User user){
        return "email" + DIVIDER + user.getEmail() +
                "provider" + DIVIDER + user.getProvider() +
                "refreshToken";
    }
}
