package com.repomgr.repomanager.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {
    private Constants() {}

    // REST Error codes
    public static final String REST_MESSAGE_CODE_ERROR = "ERROR";
    public static final String REST_MESSAGE_CODE_WARN = "WARN";
    public static final String REST_MESSAGE_CODE_INFO = "INFO";

    // User Roles
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // URI JWT check whitelist
    public static final List<String> NO_AUTH_URLS = Arrays.asList("/actuator", "/v1/authentication", "/v1/repositories/search");
}
