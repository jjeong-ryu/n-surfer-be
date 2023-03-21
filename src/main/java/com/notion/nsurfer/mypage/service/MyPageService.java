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
import com.notion.nsurfer.user.exception.UsernameAlreadyExistException;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
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
    public static final String DEFAULT_PROFILE_IMAGE = "https://res.cloudinary.com/nsurfer/image/upload/v1677038493/profile_logo_mapxvu.jpg";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");
    private final Cloudinary cloudinary;

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
        ListOperations<String, String> ops = redisTemplate.opsForList();
        List<GetWavesDto.Response.Wave> waves = new ArrayList<>();
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);

        while(startDateCal.before(endDateCal)){
            String redisWaveTimeFormat = waveDateFormat.format(startDateCal.getTime());
            String redisKey = MyPageRedisKeyUtils.makeRedisWaveTimeKey(user, redisWaveTimeFormat);
            Long redisValue = ops.size(redisKey);
            if(redisValue != null){
                GetWavesDto.Response.Wave wave = GetWavesDto.Response.Wave.builder()
                        .date(redisWaveTimeFormat)
                        .count(redisValue)
                        .build();
                waves.add(wave);
            }
            startDateCal.add(Calendar.DATE, 1);
        }
        return waves;
    }
    @Transactional
    public ResponseDto<Object> updateUserProfile(UpdateUserProfileDto.Request dto, User user) throws Exception {
        usernameValidation(dto.getNickname());
        user.update(dto);
        MultipartFile uploadedImage = dto.getImage();
        if(uploadedImage != null){
            String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider()), "_");
            Map uploadResponse = cloudinary.uploader().upload(uploadedImage, ObjectUtils.asMap("public_id", imageName));
            user.updateImage(uploadResponse.get("url").toString());
        }
        if(dto.getIsBasicImage()){
            cloudinary.api().deleteResources(List.of(user.getThumbnailImageUrl()), null);
            user.updateImage(DEFAULT_PROFILE_IMAGE);
        }
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(null).build();
    }
    private void usernameValidation(String username){
        if(userRepository.findByNickname(username).isPresent()){
            throw new UsernameAlreadyExistException();
        }
    }
}
