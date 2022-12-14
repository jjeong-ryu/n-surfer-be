package com.notion.nsuffer.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;

@Builder
public final class ResponseDto<T> {
    @JsonUnwrapped
    private ResponseCode responseCode;

    private T data;
}
