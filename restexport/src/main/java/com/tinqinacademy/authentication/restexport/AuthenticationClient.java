package com.tinqinacademy.authentication.restexport;

import com.tinqinacademy.authentication.api.operations.getuser.GetUserInput;
import com.tinqinacademy.authentication.api.operations.getuser.GetUserOutput;
import com.tinqinacademy.authentication.api.operations.getuserdetails.GetUserDetailsInput;
import com.tinqinacademy.authentication.api.operations.getuserdetails.GetUserDetailsOutput;
import com.tinqinacademy.authentication.api.paths.AuthFeignClientApiPaths;
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

    @RequestLine(AuthFeignClientApiPaths.GET_USER_DETAILS)
    GetUserDetailsOutput getUserDetails(GetUserDetailsInput input);

    @RequestLine(AuthFeignClientApiPaths.GET_USER)
    GetUserOutput getUser(GetUserInput input);
}
