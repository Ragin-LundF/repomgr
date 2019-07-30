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

    public TokenDto(String token){
        this.token = token;
    }

    public TokenDto(String token, String userId){
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
