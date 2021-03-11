package com.repomgr.repomanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;

@ConfigurationProperties(prefix="repomanager")
public class RepoManagerProperties {
    private Security security;

    public Security getSecurity() {
        if (security == null) {
            security = new Security();
        }
        return security;
    }

    public void setSecurity(final Security security) {
        this.security = security;
    }

    public static class Security {
        private String headerName;
        private String signingKey;
        private Long tokenExpirationTime;

        public String getHeaderName() {
            if (ObjectUtils.isEmpty(headerName)) {
                headerName = HttpHeaders.AUTHORIZATION;
            }
            return headerName;
        }

        public void setHeaderName(final String headerName) {
            this.headerName = headerName;
        }

        public String getSigningKey() {
            return signingKey;
        }

        public void setSigningKey(final String signingKey) {
            this.signingKey = signingKey;
        }

        public Long getTokenExpirationTime() {
            return tokenExpirationTime;
        }

        public void setTokenExpirationTime(final Long tokenExpirationTime) {
            this.tokenExpirationTime = tokenExpirationTime;
        }
    }
}
