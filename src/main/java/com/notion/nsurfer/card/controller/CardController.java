package com.notion.nsurfer.card.controller;

import com.notion.nsurfer.card.dto.GetCardDto;
import com.notion.nsurfer.card.dto.GetCardsDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseDto<PostCardDto.Response> postCard(
            @RequestPart("postCard") PostCardDto.Request dto,
            @RequestPart(value = "imgFiles", required = false) List<MultipartFile> imgFiles,
            @AuthenticationPrincipal User user
            ) throws IOException {
        return cardService.postCard(dto, imgFiles, user);
    }

    @GetMapping
    public ResponseDto<GetCardsDto.Response> getCards(
            @RequestParam(required = false, defaultValue = "") String username,
            @RequestParam(required = false, defaultValue = "") String numberOfCards,
            @RequestParam(required = false, defaultValue = "") String nextCardId
    ){
        return cardService.getCards(username, numberOfCards, nextCardId);
    }
    @GetMapping("/{cardId}")
    public ResponseDto<GetCardDto.Response> getCard(@PathVariable UUID cardId){
        return cardService.getCard(cardId);
    }

    @PatchMapping("/{cardId}")
    public ResponseDto<Object> updateCard(@PathVariable UUID cardId,
                                          @RequestPart("updateCard") PostCardDto.Request dto,
                                          @RequestPart(value = "imgFiles", required = false) List<MultipartFile> addedImages,
                                          @RequestPart(required = false) List<String> deletedImages,
                                          @AuthenticationPrincipal User user
    ) throws Exception {
        return cardService.updateCard(cardId, dto, addedImages, deletedImages, user);
    }
    @DeleteMapping("/{cardId}")
    public ResponseDto<Object> deleteCard(@PathVariable UUID cardId,
                                          @AuthenticationPrincipal User user) throws Exception {
        return cardService.deleteCard(cardId, user);
    }
}
