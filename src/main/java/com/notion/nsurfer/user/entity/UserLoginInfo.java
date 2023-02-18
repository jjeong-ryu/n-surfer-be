package com.notion.nsurfer.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 추후 Redis 도입 시 사용 될 Entity
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserLoginInfo {
    @Id @GeneratedValue
    private Long id;

    private String email;

    private String provider;

    private String accessToken;
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

}
