package com.shadow2y.luthen.api.contracts;

import lombok.Data;

@Data
public class ClientRefreshRequest {
    String clientId;
    String clientName;
    String clientSecret;
}
