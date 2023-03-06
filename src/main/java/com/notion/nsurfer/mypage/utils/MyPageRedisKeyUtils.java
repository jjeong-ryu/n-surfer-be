package com.notion.nsurfer.mypage.utils;

import com.notion.nsurfer.user.entity.User;

public class MyPageRedisKeyUtils {
    public static final String DIVIDER = ":";

    public static String makeRedisWaveTimeKey(User user, String redisWaveTimeFormat){
        return "email" + DIVIDER + user.getEmail() + DIVIDER +
                "provider" + DIVIDER + user.getProvider() + DIVIDER +
                "wave" +  DIVIDER + "date" + DIVIDER + redisWaveTimeFormat;
    }
}
