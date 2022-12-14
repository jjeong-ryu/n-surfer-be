package com.notion.nsuffer.card.controller;

import com.notion.nsuffer.card.dto.GetCardDto;
import com.notion.nsuffer.card.dto.GetCardListDto;
import com.notion.nsuffer.card.service.CardService;
import com.notion.nsuffer.common.ResponseDto;
import com.notion.nsuffer.mypage.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseDto<Object> postCard(){
        return cardService.postCard();
    }
    @GetMapping("/list")
    public ResponseDto<GetCardListDto.Response> getCardList(){
        return cardService.getCardList();
    }
    @GetMapping("/{cardId}")
    public ResponseDto<GetCardDto.Response> getCard(@PathVariable Long cardId,
                                                    @AuthenticationPrincipal User user){
        return cardService.getCard(cardId);
    }

    @PatchMapping("/{cardId}")
    public ResponseDto<Object> updateCard(@PathVariable Long cardId){
        return cardService.updateCard();
    }
    @DeleteMapping("/{cardId}")
    public ResponseDto<Object> deleteCard(@PathVariable Long cardId){
        return cardService.deleteCard();
    }
}
