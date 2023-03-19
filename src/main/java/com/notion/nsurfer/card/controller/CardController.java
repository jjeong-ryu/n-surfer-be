package com.notion.nsurfer.card.controller;

import com.notion.nsurfer.card.dto.GetCardDto;
import com.notion.nsurfer.card.dto.GetCardListDto;
import com.notion.nsurfer.card.dto.PostCardDto;
import com.notion.nsurfer.card.dto.UpdateCardDto;
import com.notion.nsurfer.card.service.CardService;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseDto<Object> postCard(
            @RequestPart("postCard") PostCardDto.Request dto,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal User user
            ) throws IOException {
        return cardService.postCard(dto, files, user);
    }

    @GetMapping("/list")
    public ResponseDto<GetCardListDto.Response> getCards(){
        return cardService.getCards();
    }
    @GetMapping("/{cardId}")
    public ResponseDto<GetCardDto.Response> getCard(@PathVariable Long cardId){
        return cardService.getCard(cardId);
    }

    @PatchMapping("/{cardId}")
    public ResponseDto<Object> updateCard(@PathVariable Long cardId,
                                          @RequestPart UpdateCardDto.Request request,
                                          @RequestPart List<MultipartFile> files,
                                          @AuthenticationPrincipal User user
    ) throws Exception {
        return cardService.updateCard(cardId, request, files, user);
    }
    @DeleteMapping("/{cardId}")
    public ResponseDto<Object> deleteCard(@PathVariable Long cardId){
        return cardService.deleteCard(cardId);
    }
}
