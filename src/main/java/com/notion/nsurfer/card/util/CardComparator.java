package com.notion.nsurfer.card.util;

import com.notion.nsurfer.card.dto.GetCardsDto;
import com.notion.nsurfer.card.dto.GetCardsDto.Response.Card;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2) {
        return o2.getLastEditDate().getNano() - o1.getLastEditDate().getNano();
    }
}
