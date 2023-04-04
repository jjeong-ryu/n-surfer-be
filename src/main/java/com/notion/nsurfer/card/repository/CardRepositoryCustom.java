package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepositoryCustom {
    List<Card> findCardsWithWaveByUserId(UUID userId);
    Optional<Card> findByIdWithImages(UUID cardId);
}
