package com.notion.nsurfer.mypage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.card.dto.UpdateCardToNotionDto;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.mapper.CardMapper;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

import static com.notion.nsurfer.common.utils.NotionUtils.NOTION_CARD_URL;
import static com.notion.nsurfer.common.utils.NotionUtils.apiKey;
import static com.notion.nsurfer.common.utils.NotionUtils.dbId;

@Service
@RequiredArgsConstructor
public class MyPageService {
    public static final String DEFAULT_PROFILE_IMAGE = "https://res.cloudinary.com/nsurfer/image/upload/v1677038493/profile_logo_mapxvu.jpg";
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    private final Cloudinary cloudinary;
    private final CardMapper cardMapper;
    private final UserMapper userMapper;

    private final RedisTemplate<String, String> redisTemplate;

    private final String VERSION = "2022-06-28";

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user){
        Integer totalWave = getTotalWaves(user);
        Integer todayWave = getTodayWave(user);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE_USING_ACCESS_TOKEN)
                .data(userMapper.getUserProfileToResponse(user,totalWave,todayWave))
                .build();
    }
    private Integer getTotalWaves(User user) {
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String total = opsForHash.get(redisWavesKey, "total");
        return total != null ? Integer.valueOf(total) : 0;
    }
    private Integer getTodayWave(User user) {
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String redisWaveHashKey = LocalDate.now().toString().replace("-", "");
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String todayWave = opsForHash.get(redisWavesKey, redisWaveHashKey);
        return todayWave != null ? Integer.valueOf(todayWave) : 0;
    }

    @Transactional
    public ResponseDto<UpdateUserProfileDto.Response> updateProfile(UpdateUserProfileDto.Request dto, MultipartFile image, User user) throws Exception {
        updateNickName(dto.getNickname(), user);
        if(dto.getIsBasicImg()){
            updateImageToBasicImage(user);
        }
        if(image != null){
            updateImageToNewImage(image, user);
        }

        return ResponseDto.<UpdateUserProfileDto.Response>builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(UpdateUserProfileDto.Response.builder()
                        .userId(user.getId())
                        .build())
                .build();
    }

    private void updateImageToNewImage(MultipartFile image, User user) throws Exception {
        cloudinary.api().deleteResources(List.of(user.getThumbnailImageName()), null);
        String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider(),
                UUID.randomUUID().toString().substring(16)), "_");
        Map uploadResponse = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("public_id", imageName));
        user.updateImage(uploadResponse.get("url").toString(), imageName);
    }

    private void updateImageToBasicImage(User user) throws Exception {
        cloudinary.api().deleteResources(List.of(user.getThumbnailImageName()), null);
        user.updateImage(DEFAULT_PROFILE_IMAGE, null);
    }

    private void updateNickName(String nickname, User user) {
        if(!user.getNickname().equals(nickname)){
            usernameValidation(nickname);
        }
        user.updateNickname(nickname);
        updateCardNickNameToNewNickname(user, nickname);
    }

    private void updateCardNickNameToNewNickname(User user, String nickname) {
        List<Card> cards = cardRepository.findByUser(user);
        for (Card card : cards) {
            updateCardNickNameOnNotionDB(card.getId(), nickname);
        }
    }

    private void usernameValidation(String username){
        if(userRepository.findByNickname(username).isPresent()){
            throw new UsernameAlreadyExistException();
        }
    }

    private void updateCardNickNameOnNotionDB(UUID cardId, String nickname) {
        WebClient webClient = cardWebclientBuilder(cardId.toString());
        webClient.patch()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.updateCardNicknameToRequest(nickname, dbId))
                .retrieve()
                .bodyToMono(UpdateCardToNotionDto.Response.class)
                .block();
    }

    private WebClient cardWebclientBuilder(String url){
        return WebClient.builder()
                .baseUrl(NOTION_CARD_URL + url)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
