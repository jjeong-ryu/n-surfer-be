package com.notion.nsurfer.mypage.utils;

import com.notion.nsurfer.user.entity.User;

public class MyPageRedisKeyUtils {
    public static final String DIVIDER = ":";

    public static String makeRedisWaveKey(User user){
        return "wave" + DIVIDER + "email" + DIVIDER
                + user.getEmail() + DIVIDER +
                "provider" + DIVIDER + user.getProvider();
    }
}
