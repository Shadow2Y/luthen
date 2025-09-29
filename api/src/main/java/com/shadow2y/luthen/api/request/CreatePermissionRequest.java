package com.shadow2y.luthen.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class CreatePermissionRequest {

    @Valid @NotNull
    public String name;

    @Valid @NotNull
    public String description;

}
