package com.notion.nsuffer.card.service;

import com.notion.nsuffer.card.dto.GetCardDto;
import com.notion.nsuffer.card.dto.GetCardListDto;
import com.notion.nsuffer.card.repository.CardRepository;
import com.notion.nsuffer.common.ResponseCode;
import com.notion.nsuffer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    public ResponseDto<GetCardDto.Response> getCard(Long cardId){
        return ResponseDto.<GetCardDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(null)
                .build();
    }

    public ResponseDto<GetCardListDto.Response> getCardList() {
        return ResponseDto.<GetCardListDto.Response>builder()
                .responseCode(ResponseCode.GET_CARD_LIST)
                .data(null)
                .build();
    }

    public ResponseDto<Object> postCard(){
        return ResponseDto.builder()
                .responseCode(ResponseCode.POST_CARD)
                .data(null).build();
    }

    public ResponseDto<Object> updateCard(){
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
