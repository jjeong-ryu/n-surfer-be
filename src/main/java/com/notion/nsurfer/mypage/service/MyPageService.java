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
    private final Cloudinary cloudinary;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user) {
        final String email = user.getEmail();
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_MY_PAGE_PROFILE)
                .data(userMapper.getUserProfileToResponse(userRepository.findByEmail(email)))
                .build();
    }

    @Transactional
    public ResponseDto<Object> updateUserProfile(UpdateUserProfileDto.Request dto) throws IOException {
        User user = userRepository.findById(dto.getUserInfo().getId())
                .orElseThrow(UserNotFoundException::new);
        user.update(dto);
        MultipartFile uploadedImage = dto.getImage();
        if(uploadedImage != null){
            String imageName = StringUtils.join(List.of(dto.getUserInfo().getEmail(), dto.getUserInfo().getProvider()), "_");
            Map uploadResponse = cloudinary.uploader().upload(uploadedImage, ObjectUtils.asMap("public_id", imageName));
            user.updateImage(uploadResponse.get("url").toString());
        }
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(null).build();
    }

    public ResponseDto<GetWavesDto.Response> getWaves(User user, Integer month){
        Calendar startDate = getStartDateCal(month);
        Calendar endDate = getEndDateCal();
        List<GetWavesDto.Response.Wave> waves = getWavesWithDate(startDate, endDate, user);
        return ResponseDto.<GetWavesDto.Response>builder()
                .responseCode(ResponseCode.GET_WAVES)
                .data(GetWavesDto.Response.builder()
                     .waves(waves).build())
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

    private List<GetWavesDto.Response.Wave> getWavesWithDate(Calendar startDateCal, Calendar endDateCal, User user){
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        List<GetWavesDto.Response.Wave> waves = new ArrayList<>();
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
