package com.repomgr.repomanager.infrastructure.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "REPO_USER")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", length = 50, nullable = false)
    private long id;
    @Column(name = "USERNAME", length = 100, nullable = false)
    private String username;
    @Column(name = "PASSWORD", length = 255, nullable = false)
    private String password;
    @Column(name = "USER_ID", length = 100, nullable = false)
    private String userId;
    @Column(name = "PROJECT_NAME", length = 100, nullable = false)
    private String projectName;
    @Column(name = "ROLE", length = 100, nullable = false)
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
