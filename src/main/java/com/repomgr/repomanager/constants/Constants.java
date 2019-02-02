package com.repomgr.repomanager.constants;

import java.util.Arrays;
import java.util.List;

public interface Constants {
    String REST_MESSAGE_CODE_ERROR = "ERROR";
    String REST_MESSAGE_CODE_WARN = "WARN";
    String REST_MESSAGE_CODE_INFO = "INFO";

    String ROLE_USER = "USER";
    String ROLE_ADMIN = "ADMIN";

    List<String> NO_AUTH_URLS = Arrays.asList("/actuator", "/authentication");
}
