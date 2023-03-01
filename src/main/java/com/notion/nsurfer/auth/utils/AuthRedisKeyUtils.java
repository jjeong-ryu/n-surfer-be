package com.notion.nsurfer.auth.utils;

import com.notion.nsurfer.user.entity.User;

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

    public static String makeRedisWaveTimeKey(User user, String redisWaveTimeFormat){
        return "email" + DIVIDER + user.getEmail() + DIVIDER +
                "provider" + DIVIDER + user.getProvider() + DIVIDER +
                "wave" +  DIVIDER + "date" + DIVIDER + redisWaveTimeFormat;
    }
}
