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
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public ResponseDto<GetWavesDto.Response> getWaves(String username, Integer month){
        Calendar startDate = getStartDateCal(month);
        Calendar endDate = getEndDateCal();
        User user = userRepository.findByusername(username)
                .orElseThrow(UserNotFoundException::new);
        List<GetWavesDto.Response.Wave> waves = getWavesWithDate(startDate, endDate, user);
        return ResponseDto.<GetWavesDto.Response>builder()
                .responseCode(ResponseCode.GET_WAVES)
                .data(GetWavesDto.Response.builder()
                    .totalWaves(getTotalWaves(user))
                    .waves(waves).build())
                .build();
    }

    private Integer getTotalWaves(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String total = opsForHash.get(redisWavesKey, "total");
        return total != null ? Integer.valueOf(total) : 0;
    }

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user){
        Integer totalWave = getTotalWaves(user);
        Integer todayWave = getTodayWave(user);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE_USING_ACCESS_TOKEN)
                .data(userMapper.getUserProfileToResponse(user,totalWave,todayWave))
                .build();
    }

    private Integer getTodayWave(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String redisWaveHashKey = waveDateFormat.format(new Date());
        String todayWave = opsForHash.get(redisWavesKey, redisWaveHashKey);
        return todayWave != null ? Integer.valueOf(todayWave) : 0;
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
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        List<GetWavesDto.Response.Wave> waves = new ArrayList<>();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        while(startDateCal.before(endDateCal)){
            String redisWaveTimeFormat = waveDateFormat.format(startDateCal.getTime());
            String waveNum = opsForHash.get(redisWavesKey, redisWaveTimeFormat);
            if(waveNum != null){
                GetWavesDto.Response.Wave wave = GetWavesDto.Response.Wave.builder()
                        .date(redisWaveTimeFormat)
                        .count(Integer.valueOf(waveNum))
                        .build();
                waves.add(wave);
            }
            startDateCal.add(Calendar.DATE, 1);
        }
        return waves;
    }
    @Transactional
    public ResponseDto<Object> updateProfile(UpdateUserProfileDto.Request dto, MultipartFile image, User user) throws Exception {
        usernameValidation(dto.getUsername());
        user.update(dto);
        if(image != null){
            String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider()), "_");
            Map uploadResponse = cloudinary.uploader().upload(image, ObjectUtils.asMap("public_id", imageName));
            user.updateImage(uploadResponse.get("url").toString());
        }
        if(dto.getIsBasicImage()){
            cloudinary.api().deleteResources(List.of(user.getThumbnailImageUrl()), null);
            user.updateImage(DEFAULT_PROFILE_IMAGE);
        }
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(UpdateUserProfileDto.Response.builder()
                        .userId(user.getId())
                        .build())
                .build();
    }
    private void usernameValidation(String username){
        if(userRepository.findByusername(username).isPresent()){
            throw new UsernameAlreadyExistException();
        }
    }
}
