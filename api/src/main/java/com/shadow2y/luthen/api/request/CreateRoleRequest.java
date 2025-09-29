package com.shadow2y.luthen.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateRoleRequest {

    @Valid @NotNull
    public String name;

    @Valid @NotNull
    public String description;

    public List<String> permissions;

}
