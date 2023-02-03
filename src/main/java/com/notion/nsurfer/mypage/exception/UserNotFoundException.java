package com.notion.nsurfer.mypage.exception;

import com.notion.nsurfer.common.BusinessException;
import com.notion.nsurfer.common.ResponseCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ResponseCode.ERROR_USER_NOT_FOUND);
    }
}
