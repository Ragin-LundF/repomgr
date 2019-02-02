package com.repomgr.repomanager.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    @NotNull
    @Size(min = 1, max = 100)
    private String username;
    @NotNull
    @Size(min = 8, max = 255)
    private String password;
    @Size(max = 100)
    private String userId;
    @Size(max = 100)
    private String projectName;
    @Size(max = 100)
    private String role;
    @JsonIgnore
    private Boolean isValid = false;

    public UserDto() {
    }

    public UserDto(boolean isValid) {
        this.isValid = isValid;
    }

    public UserDto(boolean isValid, String userId) {
        this.isValid = isValid;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
