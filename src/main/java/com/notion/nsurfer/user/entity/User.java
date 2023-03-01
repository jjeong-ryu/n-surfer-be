package com.notion.nsurfer.user.entity;

import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.common.config.Authority;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id @GeneratedValue
    private Long id;

    private String nickname;

    private String provider;

    private String email;

    @Temporal(value = TemporalType.DATE)
    private Date birthday;

    private String password;

    private String thumbnailImageUrl;

    @Builder.Default
    private Boolean isDeleted = false;

    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private Authority authority = Authority.USER;

    public void delete(){
        this.isDeleted = true;
    }
    @OneToMany(mappedBy = "user")
    private List<Card> cards;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void update(UpdateUserProfileDto.Request dto){
        this.nickname = dto.getUserInfo().getNickname();
    }

    public void updateImage(String url){
        this.thumbnailImageUrl = url;
    }
}
