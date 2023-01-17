package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
