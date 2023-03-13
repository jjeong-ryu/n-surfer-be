package com.notion.nsurfer.card.service;

import com.notion.nsurfer.card.dto.GetCardDto;
import com.notion.nsurfer.card.dto.GetCardListDto;
import com.notion.nsurfer.card.dto.PostCardDto;
import com.notion.nsurfer.card.dto.PostCardToNotionDto;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.exception.CardNotFoundException;
import com.notion.nsurfer.card.mapper.CardMapper;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final String NOTION_URL = "https://api.notion.com/v1/pages/";
    private final CardMapper cardMapper;

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

    public ResponseDto<GetCardListDto.Response> getCardList() {
        List<Card> cards = cardRepository.findAll();
        // 현재 DB에 저장된 모든 카드 return
        return ResponseDto.<GetCardListDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(null)
                .build();
    }

    @Transactional
    public ResponseDto<Object> postCard(PostCardDto.Request dto, List<MultipartFile> files, User user){
        // card(page)를 노션에 저장하고, 해당 id를 db에 저장
        WebClient webClient = webclientBuilder("");
        PostCardToNotionDto.Response result = webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(cardMapper.postCardToRequest(dto, user.getId(), dbId))
                .retrieve()
                .bodyToMono(PostCardToNotionDto.Response.class)
                .block();
        // 동시에 이미지를 테이블에 cloudinary에 저장

        // wave 추가
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ResponseDto.builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(null).build();
    }

    @Transactional
    public ResponseDto<Object> updateCard(){
        // 먼저 노션에 card update 요청 보냄
        // 이 후, 백엔드 DB에 저장
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_CARD)
                .data(null).build();
    }

    @Transactional
    public ResponseDto<Object> deleteCard(Long cardId){
        WebClient webClient = webclientBuilder("");
        PostCardToNotionDto.Response result = webClient.post()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PostCardToNotionDto.Response.class)
                .block();
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
