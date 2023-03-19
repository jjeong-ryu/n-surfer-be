package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepositoryCustom {
    List<Card> findCardsWithWaveByUserId(Long userId);
    Optional<Card> findByIdWithImages(Long userId);
}
