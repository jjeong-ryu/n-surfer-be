package com.notion.nsurfer.card.exception;

import com.notion.nsurfer.common.BusinessException;
import com.notion.nsurfer.common.ResponseCode;

public class CardNotFoundException extends BusinessException {
    public CardNotFoundException(){
        super(ResponseCode.CARD_NOT_FOUND);
    }
}
