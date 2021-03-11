package com.repomgr.repomanager.rest.model.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * UserDTO which represents an user.
 * <br />
 * This DTO can be used in most user contexts.
 * <br />
 * It also contains a valid parameter, which can be used for responses.
 */
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
    private Boolean valid = false;

    public UserDto() {
    }

    public UserDto(final boolean valid) {
        this.valid = valid;
    }

    public UserDto(final boolean valid, final String userId) {
        this.valid = valid;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public boolean isValid() {
        return (getValid() != null) && getValid();
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(final Boolean valid) {
        this.valid = valid;
    }
}
