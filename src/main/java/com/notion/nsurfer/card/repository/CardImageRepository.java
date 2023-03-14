package com.notion.nsurfer.card.repository;

import com.notion.nsurfer.card.entity.CardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardImageRepository extends JpaRepository<CardImage, Long>, CardImageRepositoryCustom {
}
