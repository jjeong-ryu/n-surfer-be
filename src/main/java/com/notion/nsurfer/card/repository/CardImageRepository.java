package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.CardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CardImageRepository extends JpaRepository<CardImage, UUID>, CardImageRepositoryCustom {
}
