package com.notion.nsuffer.user.mapper;

import com.notion.nsuffer.auth.common.AuthUtil;
import com.notion.nsuffer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsuffer.common.CommonMapperConfig;
import com.notion.nsuffer.user.dto.SignUpDto;
import com.notion.nsuffer.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config = CommonMapperConfig.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto.Request request);
    @Mapping(target = "nickname", source = "response.kakaoAccount.profile.nickname")
    @Mapping(target = "email", source = "response.kakaoAccount.email")
    @Mapping(target = "provider", defaultValue = "KAKAO")
    @Mapping(target = "authority", ignore = true)
    SignUpDto.Request signUpKakaoToRequest(AuthKakaoLoginProfileDto.Response response);
//    @Mapping(target = "nickname", ignore = true)
//    SignUpDto.Request signUpGoogleToRequest(AuthKakaoLoginProfileDto.Response response);
}
