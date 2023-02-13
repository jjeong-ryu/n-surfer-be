package com.notion.nsurfer.security.util;

import com.notion.nsurfer.security.VerifyResult;
import com.notion.nsurfer.security.exception.ExpiredJwtTokenException;
import com.notion.nsurfer.security.exception.InvalidJwtException;
import com.notion.nsurfer.security.exception.JwtExceptionMessage;
import com.notion.nsurfer.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static com.notion.nsurfer.security.exception.JwtExceptionMessage.*;

@Component
public class JwtUtil implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final long AUTH_TIME = 10;
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7;
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static SecretKey key;
    private static String secret;
    private static long tokenValidityInMilliSeconds;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds){
                this.secret = secret;
                this.tokenValidityInMilliSeconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    private static SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.HS512;
    }

    public static String createAccessToken(User user) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + JwtUtil.tokenValidityInMilliSeconds + AUTH_TIME);
        return Jwts.builder().setSubject(user.getUsername() + "_" + user.getProvider())
                .claim("exp", Instant.now().getEpochSecond() + AUTH_TIME)
                .signWith(key, getAlgorithm())
                .setExpiration(validity)
                .compact();
    }

    public static String createRefreshToken(User user) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + JwtUtil.tokenValidityInMilliSeconds + REFRESH_TIME);
        return Jwts.builder().setSubject(user.getUsername() + "_" + user.getProvider())
                .claim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
                .signWith(key, getAlgorithm())
                .setExpiration(validity)
                .compact();
    }

    public static String resolveToken(HttpServletRequest request) throws InvalidJwtException {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken == null) {
            throw new InvalidJwtException("Authorization header is null");
        }

        if (!bearerToken.startsWith("Bearer ")) {
            throw new InvalidJwtException("Authorization type must be 'Bearer'");
        }

        return bearerToken.substring(7);
    }

    public static VerifyResult validateToken(String token){
        Jws<Claims> claimsJws = null;
        try {
            claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info(MALFORMED_JWT_EXCEPTION);
            throw new InvalidJwtException(MALFORMED_JWT_EXCEPTION);
        } catch (ExpiredJwtException e) {
            logger.info(EXPIRED_JWT_EXCEPTION);
            throw new ExpiredJwtTokenException(EXPIRED_JWT_EXCEPTION);
        } catch (UnsupportedJwtException e) {
            logger.info(UNSUPPORTED_JWT_EXCEPTION);
            throw new InvalidJwtException(UNSUPPORTED_JWT_EXCEPTION);
        } catch (IllegalArgumentException e) {
            logger.info(ILLEGAL_ARGUMENT_EXCEPTION);
            throw new InvalidJwtException(ILLEGAL_ARGUMENT_EXCEPTION);
        }
        return VerifyResult.builder().success(true).emailAndProvider(claimsJws.getBody().getSubject()).build();
    }

    public static String extractSubjectFromRequest(HttpServletRequest request){
        String token = resolveToken(request);
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getSubject();
    }
}
