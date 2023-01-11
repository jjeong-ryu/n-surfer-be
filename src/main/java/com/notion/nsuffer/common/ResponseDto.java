package com.notion.nsuffer.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class ResponseDto<T> {
    @JsonUnwrapped
    private ResponseCode responseCode;

    @Setter
    private T data;
}
