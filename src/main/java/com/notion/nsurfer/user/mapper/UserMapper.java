package com.notion.nsurfer.user.mapper;

import com.notion.nsurfer.auth.dto.AuthKakaoLoginProfileDto;
import com.notion.nsurfer.common.CommonMapperConfig;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.Wave;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.util.*;

@Mapper(config = CommonMapperConfig.class)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "birthday", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "thumbnailImageName", defaultValue = "Default")
    User signUpToUser(SignUpDto.Request request, String nickname);
    @Mapping(target = "nickname", source = "response.kakaoAccount.profile.username")
    @Mapping(target = "email", source = "response.kakaoAccount.email")
    @Mapping(target = "provider", defaultValue = "KAKAO")
    @Mapping(target = "thumbnailImageUrl", source = "response.kakaoAccount.profile.thumbnailImageUrl")
    @Mapping(target = "authority", ignore = true)
    @Mapping(target = "thumbnailImageName", source = "response.kakaoAccount.profile.thumbnailImageName")
    SignUpDto.Request signUpKakaoToRequest(AuthKakaoLoginProfileDto.Response response);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "provider", source = "user.provider")
    @Mapping(target = "nickname", source = "user.nickname")
    @Mapping(target = "userBirth", source = "user.birthday")
    @Mapping(target = "imgUrl", source = "user.thumbnailImageUrl")
    GetUserProfileDto.Response getUserProfileToResponse(User user, Integer totalWave, Integer todayWave);

    @Mapping(target = "userId", source = "user.id")
    DeleteUserDto.Response deleteUserToResponse(User user);
}
