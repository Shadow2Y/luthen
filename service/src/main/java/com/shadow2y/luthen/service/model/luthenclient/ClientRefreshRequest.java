package com.shadow2y.luthen.service.model.luthenclient;

import lombok.Data;

@Data
public class ClientRefreshRequest {
    String clientId;
    String clientName;
    String clientSecret;
}
