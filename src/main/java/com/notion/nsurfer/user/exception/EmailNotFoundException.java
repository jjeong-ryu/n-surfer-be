package com.notion.nsurfer.user.exception;

import com.notion.nsurfer.common.BusinessException;
import com.notion.nsurfer.common.ResponseCode;

public class EmailNotFoundException extends BusinessException {
    public EmailNotFoundException(){
        super(ResponseCode.ERROR_EMAIL_NOT_FOUND);
    }
}
