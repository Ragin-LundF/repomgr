package com.repomgr.repomanager.rest.model.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
    private List<ArtifactDto> dependencies;
    @Size(max = 512)
    private String repositoryUrl;
    @NotNull
    private Date creationDate;
    private Boolean latestVersion;
    private String description;
    @NotNull
    @Size(max = 255)
    private String type;
    @Size(max = 55)
    private String uid;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(final String branch) {
        this.branch = branch;
    }

    public ArtifactDto getArtifact() {
        return artifact;
    }

    public void setArtifact(final ArtifactDto artifact) {
        this.artifact = artifact;
    }

    public List<ArtifactDto> getDependencies() {
        return dependencies;
    }

    public void setDependencies(final List<ArtifactDto> dependencies) {
        this.dependencies = dependencies;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(final String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(final Boolean latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }
}
