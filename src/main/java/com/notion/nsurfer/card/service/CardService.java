package com.notion.nsurfer.card.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.auth.util.AuthRedisKeyUtils;
import com.notion.nsurfer.card.dto.*;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.CardImage;
import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.card.mapper.CardMapper;
import com.notion.nsurfer.card.repository.CardImageRepository;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.card.util.CardRedisKeyUtils;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.mypage.dto.GetWavesDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.mypage.utils.MyPageRedisKeyUtils;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardImageRepository cardImageRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String NOTION_CARD_URL = "https://api.notion.com/v1/pages/";
    private final String NOTION_DB_URL = "https://api.notion.com/v1/databases/";
    private final CardMapper cardMapper;
    private final Cloudinary cloudinary;
    private final SimpleDateFormat waveDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Value("${notion.token}")
    private String apiKey;
    @Value("${notion.dbId}")
    private String dbId;
    private final String VERSION = "2022-06-28";
    public ResponseDto<GetCardDto.Response> getCard(UUID cardId){
        WebClient webClient = cardWebclientBuilder(cardId.toString());
        GetCardToNotionDto.Response notionResponse = webClient.get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GetCardToNotionDto.Response.class)
                .block();
        // 해당 정보를 받아 최종적으로 프론트로 전달
        return ResponseDto.<GetCardDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(cardMapper.getCardToResponse(notionResponse))
                .build();
    }

    public ResponseDto<GetCardsDto.Response> getCards(final String username, final String numberOfCards) {
        // username이 null이어도 가능
        WebClient webClient = dbQueryWebclientBuilder();
        GetCardsToNotionDto.Response notionResponse = webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.getCardsToNotionRequest(username))
                .retrieve()
                .bodyToMono(GetCardsToNotionDto.Response.class)
                .block();
        GetCardsDto.Response responseData = cardMapper.getCardsToResponse(notionResponse, numberOfCards);

        // 현재 DB에 저장된 모든 카드 return
        return ResponseDto.<GetCardsDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(responseData)
                .build();
    }

    @Transactional
    public ResponseDto<PostCardDto.Response> postCard(PostCardDto.Request dto, List<MultipartFile> imgFiles, User user) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        List<String> imageNames = new ArrayList<>();
        // 이미지 업로드 및 이미지 url 저장
        if(imgFiles != null){
            for (int idx = 0; idx < imgFiles.size(); idx++) {
                MultipartFile image = imgFiles.get(idx);
                String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider(), UUID.randomUUID().toString()), "_");
                Map uploadResponse = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("public_id", imageName));
                String url = uploadResponse.get("url").toString();
                imageUrls.add(url);
                imageNames.add(imageName);
            }
        }
        // card(page)를 노션에 저장하고, 해당 id를 db에 저장
        WebClient webClient = cardWebclientBuilder("");
        PostCardToNotionDto.Response notionResponse = webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.postCardToRequest(dto, user.getId(), dbId, imageUrls, imageNames))
                .retrieve()
                .bodyToMono(PostCardToNotionDto.Response.class)
                .block();
        Card card = cardRepository.save(Card.builder()
                .id(UUID.fromString(notionResponse.getCardId()))
                .user(user).build());
        for (int idx = 0; idx < imageUrls.size(); idx++) {
            CardImage cardImage = CardImage.builder()
                    .id(UUID.randomUUID())
                    .url(imageUrls.get(idx))
                    .card(card)
                    .cardImageName(imageNames.get(idx)).build();
            cardImageRepository.save(cardImage);
        }

        // wave에 오늘 날짜 잔디 수 value + 1, total +1,  cardId에 생성이력 추가
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        ListOperations<String, String> opsForList = redisTemplate.opsForList();

        String waveKey = MyPageRedisKeyUtils.makeRedisWaveKey(user);
        String redisWaveTimeFormat = waveDateFormat.format(new Date());

        String strWaveNum = opsForHash.get(waveKey,redisWaveTimeFormat);
        int intWaveNum = strWaveNum != null ? Integer.valueOf(strWaveNum) : 0;
        opsForHash.put(waveKey, redisWaveTimeFormat, String.valueOf(intWaveNum + 1));

        String strTotal = opsForHash.get(waveKey,"total");
        int intTotal = strTotal != null ? Integer.valueOf(strTotal) : 0;
        opsForHash.put(waveKey, "total", String.valueOf(intTotal + 1));

        String historyValue = "create:" + CardRedisKeyUtils.makeRedisCardHistoryValue(card.getId(), LocalDate.now());
        opsForList.rightPush(notionResponse.getCardId(), historyValue);

        return ResponseDto.<PostCardDto.Response>builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(PostCardDto.Response.builder()
                        .cardId(card.getId()).build()
                )
                .build();
    }

    @Transactional
    public ResponseDto<Object> updateCard(UUID cardId, UpdateCardDto.Request dto, List<MultipartFile> addedImages, List<String> deletedImages,  User user) throws Exception {
        Card card = cardRepository.findByIdWithImages(cardId)
                .orElseThrow(CardNotFoundException::new);
        // card 수정 API
        WebClient webClient = cardWebclientBuilder("");
        UpdateCardToNotionDto.Response result = webClient.patch()
                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue()
                .retrieve()
                .bodyToMono(UpdateCardToNotionDto.Response.class)
                .block();

        List<CardImage> cardImages = card.getCardImages();
        // 이미지 제거 API
        if(deletedImages != null){
            cloudinary.api().deleteResources(deletedImages, null);
            for (String deletedImage : deletedImages) {
                CardImage cardImage = cardImages.stream().filter(ci -> ci.getCardImageName().equals(deletedImage))
                        .findFirst()
                        .orElseThrow(CardNotFoundException::new);
                cardImages.remove(cardImage);
            }
        }
        // 이미지 추가 API
        if(addedImages != null){
            for (MultipartFile addedImage : addedImages) {
                // 카드 업로드 후 받은 url을
                String imageName = StringUtils.join(List.of(user.getEmail(), user.getProvider(), UUID.randomUUID().toString()), "_");
                Map uploadResponse = cloudinary.uploader().upload(addedImage.getBytes(), ObjectUtils.asMap("public_id", imageName));
                String url = uploadResponse.get("url").toString();
                CardImage cardImage = CardImage.builder()
                        .url(url)
                        .cardImageName(imageName)
                        .card(card)
                        .build();
                cardImages.add(cardImage);
            }
        }
        // wave 추가
        ListOperations<String, String> ops = redisTemplate.opsForList();
        String timeKey = "update:" + CardRedisKeyUtils.makeRedisCardHistoryValue(cardId, LocalDate.now());
        ops.rightPush(timeKey, result.getCardId());
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_CARD)
                .data(null).build();
    }

    @Transactional
    public ResponseDto<Object> deleteCard(UUID cardId) throws Exception {
        // 카드 제거 요청 API
        WebClient webClient = cardWebclientBuilder("");
        DeleteCardDto.Response result = webClient.patch()
                .bodyValue(UpdateCardToNotionDto.Request.builder()
                        .archived(true).build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DeleteCardDto.Response.class)
                .block();

        // 관련 이미지 db에서 제거 후 cloudinary에서도 제거
        Card deletedCard = cardRepository.findByIdWithImages(cardId)
                .orElseThrow(CardNotFoundException::new);
        List<String> deletedImages = deletedCard.getCardImages().stream().map(ci -> ci.getCardImageName()).collect(Collectors.toList());
        cloudinary.api().deleteResources(deletedImages, null);
        cardRepository.delete(deletedCard);

        // cardId 순회하면서 해당 일자에 해당하는 wave:email:provider hash값 -1

        // card 제거

        return ResponseDto.builder()
                .responseCode(ResponseCode.DELETE_CARD)
                .data(null).build();
    }

    private Calendar getStartDateCal(LocalDateTime startDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(Timestamp.valueOf(startDate));
        return cal;
    }

    private Calendar getEndDateCal(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE,1);
        return cal;
    }

    private WebClient cardWebclientBuilder(String url){
        return WebClient.builder()
                .baseUrl(NOTION_CARD_URL + url)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private WebClient dbWebclientBuilder(String username){
        if(username != null){

        }
        return WebClient.builder()
                .baseUrl(NOTION_DB_URL + dbId)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    private WebClient dbQueryWebclientBuilder(){
        return WebClient.builder()
                .baseUrl(NOTION_DB_URL + dbId + "/query")
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
