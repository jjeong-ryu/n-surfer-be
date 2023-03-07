package com.notion.nsurfer.mypage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.GetWavesDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.mypage.utils.MyPageRedisKeyUtils;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    public ResponseDto<GetWavesDto.Response> getWaves(String nickname, Integer month){
        Calendar startDate = getStartDateCal(month);
        Calendar endDate = getEndDateCal();
        List<GetWavesDto.Response.Wave> waves = getWavesWithDate(startDate, endDate, nickname);
        return ResponseDto.<GetWavesDto.Response>builder()
                .responseCode(ResponseCode.GET_WAVES)
                .data(GetWavesDto.Response.builder()
                     .waves(waves).build())
                .build();
    }

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user){
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE_USING_ACCESS_TOKEN)
                .data(userMapper.getUserProfileToResponse(user))
                .build();
    }
    private Calendar getStartDateCal(Integer month){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -month);
        return cal;
    }

    private Calendar getEndDateCal(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE,1);
        return cal;
    }

    private List<GetWavesDto.Response.Wave> getWavesWithDate(Calendar startDateCal, Calendar endDateCal, String nickname){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        List<GetWavesDto.Response.Wave> waves = new ArrayList<>();
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        while(startDateCal.before(endDateCal)){
            String redisWaveTimeFormat = waveDateFormat.format(startDateCal.getTime());
            String redisKey = MyPageRedisKeyUtils.makeRedisWaveTimeKey(user, redisWaveTimeFormat);
            String redisValue = ops.get(redisKey);
            if(redisValue != null){
                GetWavesDto.Response.Wave wave = GetWavesDto.Response.Wave.builder()
                        .date(redisWaveTimeFormat)
                        .count(Integer.valueOf(redisValue))
                        .build();
                waves.add(wave);
            }
            startDateCal.add(Calendar.DATE, 1);
        }
        return waves;
    }
}
