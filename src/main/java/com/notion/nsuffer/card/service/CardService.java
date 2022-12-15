package com.notion.nsuffer.card.service;

import com.notion.nsuffer.card.dto.GetCardDto;
import com.notion.nsuffer.card.dto.GetCardListDto;
import com.notion.nsuffer.card.dto.PostCardDto;
import com.notion.nsuffer.card.entity.Card;
import com.notion.nsuffer.card.exception.CardNotFoundException;
import com.notion.nsuffer.card.repository.CardRepository;
import com.notion.nsuffer.common.ResponseCode;
import com.notion.nsuffer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    public ResponseDto<GetCardDto.Response> getCard(Long cardId){
        // id를 받아 그에 매핑되는 notion card id를 기반으로 request
        Card card = cardRepository.findById(cardId).orElseThrow(CardNotFoundException::new);
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

    public ResponseDto<Object> postCard(PostCardDto.Request dto, List<MultipartFile> files){
        // 먼저 노션에 card post 요청 보냄
        // 이 후, 해당 id를 받아 백엔드 DB에 저장
        return ResponseDto.builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(null).build();
    }

    public ResponseDto<Object> updateCard(){
        // 먼저 노션에 card update 요청 보냄
        // 이 후, 백엔드 DB에 저장
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_CARD)
                .data(null).build();
    }

    public ResponseDto<Object> deleteCard(){
        return ResponseDto.builder()
                .responseCode(ResponseCode.DELETE_CARD)
                .data(null).build();
    }
}
