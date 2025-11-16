package com.shadow2y.luthen.api.contracts;

import com.shadow2y.luthen.api.summary.RoleSummary;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ClientRefreshResponse {
    String versionId;
    Instant expiryTime;
    Set<String> blacklistedUsers;
    List<RoleSummary> roleList;
}
