package com.notion.nsurfer.user.mapper;

import com.notion.nsurfer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsurfer.common.CommonMapperConfig;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import org.mapstruct.Mapper;
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
    @Mapping(target = "thumbnailImageUrl", source = "response.kakaoAccount.profile.thumbnailImageUrl")
    @Mapping(target = "authority", ignore = true)
    SignUpDto.Request signUpKakaoToRequest(AuthKakaoLoginProfileDto.Response response);


    GetUserProfileDto.Response getUserProfileToResponse(User findUser);
//    @Mapping(target = "nickname", ignore = true)
//    SignUpDto.Request signUpGoogleToRequest(AuthKakaoLoginProfileDto.Response response);
}
