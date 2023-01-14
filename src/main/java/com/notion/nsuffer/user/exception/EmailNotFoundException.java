package com.notion.nsuffer.user.exception;

import com.notion.nsuffer.common.BusinessException;
import com.notion.nsuffer.common.ResponseCode;

public class EmailNotFoundException extends BusinessException {
    public EmailNotFoundException(){
        super(ResponseCode.ERROR_EMAIL_NOT_FOUND);
    }
}
