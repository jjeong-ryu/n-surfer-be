package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;

import java.util.List;

public interface CardRepositoryCustom {
    List<Card> findCardsWithWaveByUserId(Long userId);
}
