package com.notion.nsurfer.common;

public class BusinessException extends RuntimeException {
    private ResponseCode responseCode;

    public BusinessException(ResponseCode responseCode){
        super(responseCode.getDescription());
        this.responseCode = responseCode;
    }
}
