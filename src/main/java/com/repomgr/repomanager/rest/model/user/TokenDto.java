package com.repomgr.repomanager.rest.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import javax.validation.constraints.NotNull;

/**
 * Token DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenDto extends ResponseDto {
    @NotNull
    private String token;
    private String userId;

    public TokenDto(){
    }

    public TokenDto(final String token){
        this.token = token;
    }

    public TokenDto(final String token, final String userId){
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
