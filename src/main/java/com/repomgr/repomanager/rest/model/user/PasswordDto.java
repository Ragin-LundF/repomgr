package com.repomgr.repomanager.rest.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Password DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordDto {
    @NotNull
    @Size(min = 1, max = 255)
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
