package com.notion.nsurfer.security.exception;

public class JwtExceptionMessage {
    public static final String EXPIRED_JWT_EXCEPTION = "만료된 JWT 토큰입니다.";
    public static final String MALFORMED_JWT_EXCEPTION = "잘못된 JWT 서명입니다.";
    public static final String UNSUPPORTED_JWT_EXCEPTION = "지원되지 않는 JWT 토큰입니다.";
    public static final String ILLEGAL_ARGUMENT_EXCEPTION = "JWT 토큰이 잘못되었습니다.";
}
