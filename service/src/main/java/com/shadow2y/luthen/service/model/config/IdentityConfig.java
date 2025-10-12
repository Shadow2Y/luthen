package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import java.util.Properties;

@AllArgsConstructor
public class IdentityConfig {

    @Valid
    public final String smtpHost;

    @Valid
    public final String emailId;

    @Valid
    public final String emailPassword;

    @Valid
    public final Properties emailProperties;

    @Valid
    public final int OTPLength;

    @Valid
    public final ValkeyConfig valkeyConfig;

}
