package com.shadow2y.luthen.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSetTransformer;
import com.shadow2y.luthen.api.models.auth.UserAuth;

public class UserAuthTransformer implements JWTClaimsSetTransformer<UserAuth> {

    @Override
    public UserAuth transform(JWTClaimsSet jwtClaimsSet) {
        return null;
    }

}
