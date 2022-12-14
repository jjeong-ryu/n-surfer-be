package com.notion.nsuffer.card.exception;

import com.notion.nsuffer.common.BusinessException;
import com.notion.nsuffer.common.ResponseCode;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException(){
        super(ResponseCode.CARD_NOT_FOUND);
    }
}
