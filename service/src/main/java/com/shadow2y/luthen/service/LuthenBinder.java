package com.shadow2y.luthen.service;

import com.shadow2y.luthen.service.repository.stores.CacheStore;
import com.shadow2y.luthen.service.repository.stores.PermissionStore;
import com.shadow2y.luthen.service.repository.stores.RoleStore;
import com.shadow2y.luthen.service.repository.stores.UserStore;
import com.shadow2y.luthen.service.service.*;
import com.shadow2y.luthen.service.service.intf.PasswordService;
import com.shadow2y.luthen.service.service.intf.TokenService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class LuthenBinder extends AbstractBinder {

    private final AppConfig config;

    public LuthenBinder(AppConfig config) {
        this.config = config;
    }

    @Override
    protected void configure() {
        // Bind config and session factory
        bind(config).to(AppConfig.class);

        // Bind services
        bindAsContract(UserStore.class);
        bindAsContract(RoleStore.class);
        bindAsContract(CacheStore.class);
        bindAsContract(PermissionStore.class);

        bindAsContract(AuthService.class);
        bindAsContract(CacheService.class);
        bindAsContract(IdentityService.class);
        bindAsContract(LuthenClientService.class);
        bindAsContract(LuthenTokenService.class).to(TokenService.class);
        bindAsContract(PasswordServiceImpl.class).to(PasswordService.class);

    }
}

