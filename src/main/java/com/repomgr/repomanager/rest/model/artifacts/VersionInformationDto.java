package com.repomgr.repomanager.rest.model.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

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
    private ArtifactDto artifact;
    @NotNull
    @Size(min = 1, max = 20)
    private String version;
    private List<ArtifactDto> dependencies;
    @Size(max = 512)
    private String repositoryUrl;
    @NotNull
    private Date creationDate;
    private Boolean latestVersion;
    private String description;
    @NotNull
    private String type;

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

    public ArtifactDto getArtifact() {
        return artifact;
    }

    public void setArtifact(ArtifactDto artifact) {
        this.artifact = artifact;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<ArtifactDto> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ArtifactDto> dependencies) {
        this.dependencies = dependencies;
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

    public Boolean getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
