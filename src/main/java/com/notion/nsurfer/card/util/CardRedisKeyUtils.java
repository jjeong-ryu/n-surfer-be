package com.notion.nsurfer.card.util;

import java.time.LocalDate;
import java.util.UUID;

public class CardRedisKeyUtils {
    public static final String DIVIDER = ":";

    public static String makeRedisCardHistoryValue(UUID cardId){
        return "card" + DIVIDER + cardId;
    }
}
