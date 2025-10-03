package com.shadow2y.luthen.service.model.luthenclient;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
public class ClientRefreshResponse {
    Instant expiryTime;
    Set<String> blackListedTokens;
    Map<String, Set<String>> roleList;
}
