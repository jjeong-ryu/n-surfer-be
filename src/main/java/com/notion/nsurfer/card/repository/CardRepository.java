package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, CardRepositoryCustom {
    List<Card> findByUser(User user);
}
