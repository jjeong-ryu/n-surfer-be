package com.notion.nsurfer.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
}
