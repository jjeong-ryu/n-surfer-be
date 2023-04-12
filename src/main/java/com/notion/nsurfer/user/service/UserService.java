package com.notion.nsurfer.user.service;

import com.cloudinary.Cloudinary;
import com.notion.nsurfer.auth.util.AuthRedisKeyUtils;
import com.notion.nsurfer.card.dto.DeleteCardDto;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.CardImage;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.card.util.CardRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.GetWavesDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.mypage.utils.MyPageRedisKeyUtils;
import com.notion.nsurfer.mypage.utils.WebClientBuilder;
import com.notion.nsurfer.security.util.JwtUtil;
import com.notion.nsurfer.user.dto.DeleteUserDto;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.dto.SignInDto;
import com.notion.nsurfer.user.dto.SignUpDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.entity.UserLoginInfo;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserLoginInfoRepository;
import com.notion.nsurfer.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.notion.nsurfer.auth.common.AuthUtil.KAKAO;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final UserLoginInfoRepository userLoginInfoRepository;

    private final Cloudinary cloudinary;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    private final static String NOTION_CARD_URL = "https://api.notion.com/v1/pages/";
    @Transactional
    public SignUpDto.Response signUpWithKakao(SignUpDto.Request request) {
        String randomNickname = UUID.randomUUID().toString().replace("-", "").substring(8, 23);
        User user = userMapper.signUpToUser(request, randomNickname);
        userRepository.save(user);
        return SignUpDto.Response.builder()
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .email(request.getEmail())
                .nickname(request.getNickname()).build();
    }
    @Transactional
    public ResponseDto<DeleteUserDto.Response> deleteUser(User user) throws Exception {
        List<Card> cards = cardRepository.findCardsWithImagesByUserId(user.getId());
        WebClient webClient = WebClientBuilder.cardWebclientBuilder("");
        for (Card card : cards) {
            deleteCardFromCloudinary(webClient, card);
            deleteCardWave(card.getId());
            cardRepository.delete(card);
        }
        deleteUserWave(user);
        user.delete();
        userRepository.delete(user);
        return ResponseDto.<DeleteUserDto.Response>builder()
                .responseCode(ResponseCode.DELETE_USER)
                .data(userMapper.deleteUserToResponse(user))
                .build();
    }
    private void deleteCardFromCloudinary(WebClient webClient, Card card) throws Exception {
        List<CardImage> cardImages = card.getCardImages();
        if(cardImages.size() > 0){
            List<String> cardNames = cardImages.stream().map(CardImage::getCardImageName).collect(Collectors.toList());
            cloudinary.api().deleteResources(cardNames, null);
        }
        try {
            webClient.patch().uri(NOTION_CARD_URL + card.getId())
                    .bodyValue(DeleteCardDto.Request.builder()
                            .archived(true).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(DeleteCardDto.Response.class)
                    .block();
        } catch (Exception e){
            System.out.println("이미 삭제된 카드입니다.");
        }

    }

    private void deleteUserWave(User user) {
        redisTemplate.delete(MyPageRedisKeyUtils.makeRedisWaveKey(user));
    }

    private void deleteCardWave(UUID cardId) {
        redisTemplate.delete(CardRedisKeyUtils.makeRedisCardHistoryValue(cardId));
    }

    public ResponseDto<GetWavesDto.Response> getWaves(String nickname, String startDate){
        Calendar calStartDate = getStartDateCal(startDate);
        Calendar endDate = getEndDateCal(startDate);
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        List<GetWavesDto.Response.Wave> waves = getWavesWithDate(calStartDate, endDate, user);
        return ResponseDto.<GetWavesDto.Response>builder()
                .responseCode(ResponseCode.GET_WAVES)
                .data(GetWavesDto.Response.builder()
                        .totalWaves(getTotalWaves(user))
                        .waves(waves).build())
                .build();
    }
    private Calendar getStartDateCal(String startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(java.sql.Date.valueOf(startDate));
        return cal;
    }

    private Calendar getEndDateCal(String startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(java.sql.Date.valueOf(startDate));
        cal.add(Calendar.DATE,71);
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
    public String localSignUpForTest(SignUpDto.Request request){
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password("1234")
                .thumbnailImageUrl("hi")
                .provider(KAKAO)
                .build();
        userRepository.save(user);
        return "good";
    }
    public SignUpDto.TestResponse localSignInForTest(SignInDto.Request request){
        User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword())
                .orElseThrow(UserNotFoundException::new);
        String accessToken = JwtUtil.createAccessToken(user);
        String refreshToken = JwtUtil.createRefreshToken(user);
        saveAccessTokenToRedis(user, accessToken);
        saveRefreshTokenToRedis(user, refreshToken);
        UserLoginInfo userLoginInfo = UserLoginInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user).build();
        userLoginInfoRepository.save(userLoginInfo);
        return SignUpDto.TestResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(String nickname){
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        Integer totalWave = getTotalWaves(user);
        Integer todayWave = getTodayWave(user);
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(user, totalWave, todayWave))
                .build();
    }
    private Integer getTotalWaves(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String total = opsForHash.get(redisWavesKey, "total");
        return total != null ? Integer.valueOf(total) : 0;
    }

    private Integer getTodayWave(User user) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisWavesKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String redisWaveHashKey = LocalDate.now().toString().replace("-" ,"");
        String todayWave = opsForHash.get(redisWavesKey, redisWaveHashKey);
        return todayWave != null ? Integer.valueOf(todayWave) : 0;
    }
    // 추후 accessToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveAccessTokenToRedis(User user, String accessToken) {
        redisTemplate.opsForValue().set(AuthRedisKeyUtils.makeRedisAccessTokenKey(user), accessToken);
    }

    // 추후 refreshToken의 갯수를 늘리는 경우, key - List 형식으로 변경 필요성 있음(opsForValue)
    private void saveRefreshTokenToRedis(User user, String refreshToken) {
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(user.getId()));
    }
}
