package com.tinqinacademy.authentication.api.paths;

public final class AuthFeignClientApiPaths {
    private static final String POST = "POST";

    private static final String SPACE_SYMBOL = " ";

    public static final String VALIDATE = POST + SPACE_SYMBOL + AuthRestApiPaths.VALIDATE;
    public static final String GET_USER_DETAILS = POST + SPACE_SYMBOL + AuthRestApiPaths.GET_USER_DETAILS;
    public static final String GET_USER = POST + SPACE_SYMBOL + AuthRestApiPaths.GET_USER;
}
