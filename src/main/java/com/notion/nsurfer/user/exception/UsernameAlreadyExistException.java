package com.notion.nsurfer.user.exception;

import com.notion.nsurfer.common.BusinessException;
import com.notion.nsurfer.common.ResponseCode;

import static com.notion.nsurfer.common.ResponseCode.*;

public class UsernameAlreadyExistException extends BusinessException {
    public UsernameAlreadyExistException() {
        super(ERROR_USER_NAME_ALREADY_EXIST);
    }
}
