package com.shadow2y.luthen.service.utils;

import com.shadow2y.luthen.service.exception.Error;
import com.shadow2y.luthen.service.exception.LuthenError;
import com.shadow2y.luthen.service.model.Result;
import jakarta.mail.internet.InternetAddress;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Validate {

    public Result<Void, LuthenError> string(String... strings) {
        for(var str : strings) {
            if (str == null || str.isEmpty())
                return Result.error(new LuthenError(Error.VALIDATION_FAILED, "String validation failed!"));
        }
        return Result.empty();
    }

    public Result<Void, LuthenError> email(String email) {
        string(email);
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return Result.empty();
        } catch (Exception e) {
            return Result.error(new LuthenError(Error.VALIDATION_FAILED, "Email validation for :: "+email, e));
        }
    }

}
