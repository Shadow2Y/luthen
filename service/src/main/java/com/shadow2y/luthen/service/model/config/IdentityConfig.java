package com.shadow2y.luthen.service.model.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Valid @Min(2) @Max(8)
    public final int otpLength;

    @Valid @Min(1)
    public final long otpExpirySeconds;

}
