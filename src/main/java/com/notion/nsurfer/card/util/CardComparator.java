package com.notion.nsurfer.card.util;

import com.notion.nsurfer.card.dto.GetCardsDto.Response.Card;

import java.time.LocalDateTime;
import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2) {
        LocalDateTime firstTime = o1.getLastEditDate() == null ? o1.getCreateDate() : o1.getLastEditDate();
        LocalDateTime secondTime = o2.getLastEditDate() == null ? o2.getCreateDate() : o2.getLastEditDate();
        return secondTime.getNano() - firstTime.getNano();
    }
}
