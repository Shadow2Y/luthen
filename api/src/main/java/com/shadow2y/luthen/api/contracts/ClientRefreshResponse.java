package com.shadow2y.luthen.api.contracts;

import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Data
public class ClientRefreshResponse {
    String versionId;
    Instant expiryTime;
    Set<String> blacklistedUsers;
    Map<String, Set<String>> roleList;
}
