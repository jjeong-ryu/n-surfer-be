package com.notion.nsuffer.card.service;

import com.notion.nsuffer.card.repository.CardRepository;
import com.notion.nsuffer.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotionCardService {
    private final CardRepository cardRepository;

    public ResponseDto<Object> syncWithNotionDB(){
        return ResponseDto.builder().build();
    }
}
