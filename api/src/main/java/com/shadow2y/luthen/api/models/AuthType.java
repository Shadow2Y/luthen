package com.shadow2y.luthen.api.models;

import com.shadow2y.luthen.api.models.intf.LuthenResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthType implements LuthenResponse {
    AUTHORIZED(200),
    UNAUTHORIZED(401),
    ERROR(500),
    ;

    final int httpStatusCode;

    public String getApiResponseMessage() {
        return this.name();
    }

}
