package com.notion.nsuffer.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class ResponseDto<T> {
    @JsonUnwrapped
    private ResponseCode responseCode;

    private T data;
}
