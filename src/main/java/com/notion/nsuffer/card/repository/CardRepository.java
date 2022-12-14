package com.notion.nsuffer.card.repository;

import com.notion.nsuffer.mypage.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
