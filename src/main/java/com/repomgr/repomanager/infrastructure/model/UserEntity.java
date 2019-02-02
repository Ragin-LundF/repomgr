package com.repomgr.repomanager.infrastructure.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity(name = "repo_user")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id", length = 50, nullable = false)
    private long id;
    @Column(name = "username", length = 100, nullable = false)
    private String username;
    @Column(name = "password", length = 255, nullable = false)
    private String password;
    @Column(name = "user_id", length = 100, nullable = false)
    private String userId;
    @Column(name = "project_name", length = 100, nullable = false)
    private String projectName;
    @Column(name = "role", length = 100, nullable = false)
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
