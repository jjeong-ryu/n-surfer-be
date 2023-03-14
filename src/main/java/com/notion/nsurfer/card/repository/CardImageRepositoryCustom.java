package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.CardImage;

import java.util.List;

public interface CardImageRepositoryCustom {
    List<CardImage> findByCardId(Long cardId);
}
