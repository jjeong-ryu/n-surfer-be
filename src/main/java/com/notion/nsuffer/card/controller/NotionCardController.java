package com.notion.nsuffer.card.controller;

import com.notion.nsuffer.card.service.NotionCardService;
import com.notion.nsuffer.common.ResponseDto;
import com.notion.nsuffer.mypage.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notion")
public class NotionCardController {
    private final NotionCardService notionCardService;

    @PostMapping("/sync")
    public ResponseDto<Object> syncWithNotionDB(@AuthenticationPrincipal User user){
        return notionCardService.syncWithNotionDB(user);
    }
}
