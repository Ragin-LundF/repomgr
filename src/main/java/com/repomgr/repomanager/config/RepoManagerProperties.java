package com.repomgr.repomanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix="repomanager")
public class RepoManagerProperties {
    private Security security;

    public Security getSecurity() {
        if (security == null) {
            security = new Security();
        }
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public static class Security {
        private String headerName;
        private String signingKey;
        private Long tokenExpirationTime;

        public String getHeaderName() {
            if (StringUtils.isEmpty(headerName)) {
                headerName = HttpHeaders.AUTHORIZATION;
            }
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        public String getSigningKey() {
            return signingKey;
        }

        public void setSigningKey(String signingKey) {
            this.signingKey = signingKey;
        }

        public Long getTokenExpirationTime() {
            return tokenExpirationTime;
        }

        public void setTokenExpirationTime(Long tokenExpirationTime) {
            this.tokenExpirationTime = tokenExpirationTime;
        }
    }
}
