package com.tinqinacademy.authentication.api;

public final class AuthRestApiPaths {
    private static final String BASE = "api/v1";
    private static final String AUTH = BASE + "/auth";

    public static final String REGISTER = AUTH + "/register";
    public static final String LOGIN = AUTH + "/login";
    public static final String LOGOUT = AUTH + "/logout";
    public static final String CONFIRM_REGISTRATION = AUTH + "/confirm-registration";
    public static final String RECOVER_PASSWORD = AUTH + "/recover-password";
    public static final String CHANGE_PASSWORD = AUTH + "/change-password";
    public static final String PROMOTE = AUTH + "/promote";
    public static final String DEMOTE = AUTH + "/demote";
    public static final String VALIDATE = AUTH + "/validate";
}
