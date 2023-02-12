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
    @Mapping(target = "waves", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    User signUpToUser(SignUpDto.Request request);
    @Mapping(target = "nickname", source = "response.kakaoAccount.profile.nickname")
    @Mapping(target = "email", source = "response.kakaoAccount.email")
    @Mapping(target = "provider", defaultValue = "KAKAO")
    @Mapping(target = "thumbnailImageUrl", source = "response.kakaoAccount.profile.thumbnailImageUrl")
    @Mapping(target = "authority", ignore = true)
    SignUpDto.Request signUpKakaoToRequest(AuthKakaoLoginProfileDto.Response response);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "provider", source = "user.provider")
    @Mapping(target = "userName", source = "user.nickname")
    @Mapping(target = "userBirth", source = "user.birthday")
    @Mapping(target = "userType", source = "user.authority")
    @Mapping(target = "imgUrl", source = "user.thumbnailImageUrl")
    @Mapping(target = "waves", source = "user.waves")
    GetUserProfileDto.Response getUserProfileToResponse(User user);
//    @Mapping(target = "nickname", ignore = true)
//    SignUpDto.Request signUpGoogleToRequest(AuthKakaoLoginProfileDto.Response response);

    default List<GetUserProfileDto.Response.Wave> getUserProfileToWaves(List<Wave> waves){
        List<GetUserProfileDto.Response.Wave> waveList = new ArrayList<>();
        Map<LocalDate, Integer> waveMap = new HashMap<>();
        for(Wave wave : waves){
            LocalDate waveDate = wave.getDate();
            if(waveMap.containsKey(waveDate)){
                waveMap.put(wave.getDate(), waveMap.get(waveDate)+1);
            } else {
                waveMap.put(waveDate, 1);
            }
        }
        for(Map.Entry<LocalDate, Integer> entry : waveMap.entrySet()){
            waveList.add(this.getUserProfileToWave(entry.getKey(), entry.getValue()));
        }
        return waveList;
    }
    GetUserProfileDto.Response.Wave getUserProfileToWave(LocalDate date, Integer count);
    @Mapping(target = "userId", source = "user.id")
    DeleteUserDto.Response deleteUserToResponse(User user);
}
