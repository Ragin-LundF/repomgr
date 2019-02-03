package com.repomgr.repomanager.constants;

import java.util.Arrays;
import java.util.List;

public interface Constants {
    // REST Error codes
    String REST_MESSAGE_CODE_ERROR = "ERROR";
    String REST_MESSAGE_CODE_WARN = "WARN";
    String REST_MESSAGE_CODE_INFO = "INFO";

    // User Roles
    String ROLE_USER = "ROLE_USER";
    String ROLE_ADMIN = "ROLE_ADMIN";

    // URI JWT check whitelist
    List<String> NO_AUTH_URLS = Arrays.asList("/actuator", "/v1/authentication", "/v1/repositories/search");
}
