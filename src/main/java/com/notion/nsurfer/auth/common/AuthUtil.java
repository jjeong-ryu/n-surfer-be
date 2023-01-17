package com.notion.nsurfer.auth.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    public static String KAKAO = "KAKAO";
    public static String NAVER = "NAVER";
    public static String GOOGLE = "GOOGLE";
    public static String KAKAO_ACCESS_TOKEN_REQUEST_URL = "https://kauth.kakao.com/oauth/token";
    public static String KAKAO_PROFILE_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    public static String KAKAO_CLIENT_ID;

    @Value("${auth.kakao.client-id}")
    public void setKakaoClientId(String kakaoClientId) { this.KAKAO_CLIENT_ID = kakaoClientId;}

    public static String getClientId(String provider){
        if(provider.equals(KAKAO)){
            return KAKAO_CLIENT_ID;
        }
        return null;
    }
}
