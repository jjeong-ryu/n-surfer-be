package com.notion.nsurfer.card.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.auth.utils.AuthRedisKeyUtils;
import com.notion.nsurfer.card.dto.*;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.entity.CardImage;
import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.card.mapper.CardMapper;
import com.notion.nsurfer.card.repository.CardImageRepository;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final CardImageRepository cardImageRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String NOTION_URL = "https://api.notion.com/v1/pages/";
    private final CardMapper cardMapper;
    private final Cloudinary cloudinary;

    @Value("${notion.token}")
    private String apiKey;
    @Value("${notion.dbId}")
    private String dbId;
    private final String VERSION = "2022-06-28";
    public ResponseDto<GetCardDto.Response> getCard(Long cardId){

        // 해당 정보를 받아 최종적으로 프론트로 전달
        return ResponseDto.<GetCardDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(null)
                .build();
    }

    public ResponseDto<GetCardListDto.Response> getCards() {
        List<Card> cards = cardRepository.findAll();
        // 현재 DB에 저장된 모든 카드 return
        return ResponseDto.<GetCardListDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(null)
                .build();
    }

    @Transactional
    public ResponseDto<Object> postCard(PostCardDto.Request dto, List<MultipartFile> files, User user) throws IOException {
        // card(page)를 노션에 저장하고, 해당 id를 db에 저장
        WebClient webClient = webclientBuilder("");
        PostCardToNotionDto.Response result = webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.postCardToRequest(dto, user.getId(), dbId, files))
                .retrieve()
                .bodyToMono(PostCardToNotionDto.Response.class)
                .block();
        Card card = cardRepository.save(Card.builder()
                .notionId(result.getCardId())
                .user(user).build());

        // 동시에 이미지를 테이블에 cloudinary에 저장
        if(files != null){
            for (int idx = 0; idx < files.size(); idx++) {
                MultipartFile image = files.get(idx);
                String imageName = StringUtils.join(List.of(result.getCardId(), String.valueOf(idx)), "_");
                Map uploadResponse = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.asMap("public_id", imageName));
                for (Object o : uploadResponse.keySet()) {
                    System.out.println(o.toString());
                }
                String url = uploadResponse.get("url").toString();
                CardImage cardImage = CardImage.builder()
                        .url(url)
                        .card(card)
                        .cardName(imageName).build();
                cardImageRepository.save(cardImage);
            }
        }
        // wave 추가
        ListOperations<String, String> ops = redisTemplate.opsForList();
        String timeKey = "create:" + AuthRedisKeyUtils.makeRedisWaveTimeKey(user, LocalDate.now());
        ops.rightPush(timeKey, result.getCardId());
        return ResponseDto.builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(null).build();
    }

    @Transactional
    public ResponseDto<Object> updateCard(Long cardId, UpdateCardDto.Request dto, List<MultipartFile> files, User user) throws Exception {
        Card card = cardRepository.findByIdWithImages(cardId)
                .orElseThrow(CardNotFoundException::new);
        List<String> deletedImages = new ArrayList<>();
        // card 수정 API
        WebClient webClient = webclientBuilder("");
        UpdateCardToNotionDto.Response result = webClient.patch()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UpdateCardToNotionDto.Response.class)
                .block();
        // 이미지 제거 API
//        deletedImages.add("cld-sample-4");
//        deletedImages.add("cld-sample-3");
        cloudinary.api().deleteResources(deletedImages, null);
        // wave 추가
        ListOperations<String, String> ops = redisTemplate.opsForList();
        String timeKey = "update:" + AuthRedisKeyUtils.makeRedisWaveTimeKey(user, LocalDate.now());
        ops.rightPush(timeKey, result.getCardId());
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_CARD)
                .data(null).build();
    }

    @Transactional
    public ResponseDto<Object> deleteCard(Long cardId){
        // 카드 제거 요청 API
        WebClient webClient = webclientBuilder("");
        DeleteCardDto.Response result = webClient.patch()
                .bodyValue(UpdateCardToNotionDto.Request.builder().build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DeleteCardDto.Response.class)
                .block();


        // 관련 이미지 db에서 제거 후 cloudinary에서도 제거
        List<CardImage> images = cardImageRepository.findByCardId(cardId);

        // 관련 wave 제거(create:cardId, update:cardId 모두 제거. 시작일은 card의 createdAt을 활용)

        return ResponseDto.builder()
                .responseCode(ResponseCode.DELETE_CARD)
                .data(null).build();
    }

    private WebClient webclientBuilder(String url){
        return WebClient.builder()
                .baseUrl(NOTION_URL + url)
                .defaultHeader("Notion-Version", VERSION)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
}
