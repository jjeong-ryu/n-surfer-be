package com.notion.nsuffer.auth.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public static String KAKAO_CLIENT_ID;

    @Value("${auth.kakao.client-id}")
    public void setKakaoClientId(String kakaoClientId) { this.KAKAO_CLIENT_ID = kakaoClientId;}

}
