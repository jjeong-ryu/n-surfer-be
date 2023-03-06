package com.notion.nsurfer.card.controller;

import com.notion.nsurfer.card.dto.GetCardDto;
import com.notion.nsurfer.card.dto.GetCardListDto;
import com.notion.nsurfer.card.dto.PostCardDto;
import com.notion.nsurfer.card.service.CardService;
import com.notion.nsurfer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseDto<Object> postCard(
            @RequestPart("postCard") PostCardDto.Request dto,
            @RequestPart("file") List<MultipartFile> files
    ){
        return cardService.postCard(dto,files);
    }

    @GetMapping("/list")
    public ResponseDto<GetCardListDto.Response> getCardList(){
        return cardService.getCardList();
    }
    @GetMapping("/{cardId}")
    public ResponseDto<GetCardDto.Response> getCard(@PathVariable Long cardId){
        return cardService.getCard(cardId);
    }

    @PatchMapping("/{cardId}")
    public ResponseDto<Object> updateCard(@PathVariable Long cardId){
        return cardService.updateCard();
    }
    @DeleteMapping("/{cardId}")
    public ResponseDto<Object> deleteCard(@PathVariable Long cardId){
        return cardService.deleteCard(cardId);
    }
}
