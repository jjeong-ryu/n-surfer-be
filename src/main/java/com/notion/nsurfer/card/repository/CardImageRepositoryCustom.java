package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.CardImage;

import java.util.List;
import java.util.UUID;

public interface CardImageRepositoryCustom {
    List<CardImage> findByCardId(UUID cardId);
}
