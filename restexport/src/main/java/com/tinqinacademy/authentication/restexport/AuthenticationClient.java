package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.AuthFeignClientApiPaths;
import com.tinqinacademy.authentication.api.operations.validate.ValidateInput;
import com.tinqinacademy.authentication.api.operations.validate.ValidateOutput;
import feign.Headers;
import feign.RequestLine;

@Headers({
        "Content-Type: application/json"
})
public interface AuthenticationClient {
    @RequestLine(AuthFeignClientApiPaths.VALIDATE)
    ValidateOutput validate(ValidateInput input);
}
