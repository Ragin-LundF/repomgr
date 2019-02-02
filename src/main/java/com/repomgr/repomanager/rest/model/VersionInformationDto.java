package com.repomgr.repomanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * VersionInformation DTO for repository and artifact related information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionInformationDto {
    @NotNull
    @Size(min = 1, max = 100)
    private String projectName;
    @NotNull
    @Size(min = 1, max = 255)
    private String branch;
    @NotNull
    @Size(min = 1, max = 100)
    private String groupId;
    @NotNull
    @Size(min = 1, max = 100)
    private String artifactId;
    @NotNull
    @Size(min = 1, max = 20)
    private String version;
    @Size(max = 512)
    private String repositoryUrl;
    @NotNull
    private Date creationDate;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
