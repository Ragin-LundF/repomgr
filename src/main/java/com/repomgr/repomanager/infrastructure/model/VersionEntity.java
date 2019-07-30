package com.repomgr.repomanager.infrastructure.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity(name = "REPO_VERSION")
public class VersionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "ID", length = 50, nullable = false)
    private Long id;
    @Column(name = "PROJECT_NAME", length = 100, nullable = false)
    private String projectName;
    @Column(name = "BRANCH", length = 255, nullable = false)
    private String branch;
    @Column(name = "GROUP_ID", length = 100, nullable = false)
    private String groupId;
    @Column(name = "ARTIFACT_ID", length = 100, nullable = false)
    private String artifactId;
    @Column(name = "VERSION", length = 20, nullable = false)
    private String version;
    @Column(name = "REPOSITORY_URL", length = 512)
    private String repositoryUrl;
    @Column(name = "CREATION_DATE", nullable = false)
    private Date creationDate;
    @Column(name = "DESCRIPTION", nullable = true)
    private String description;
    @Column(name = "TYPE", nullable = false)
    private String type;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "REPO_DEPENDENCY_JOIN",
            joinColumns = @JoinColumn(name = "ARTIFACT_ID"),
            inverseJoinColumns = @JoinColumn(name = "DEPENDENCY_ID")
    )
    private List<VersionEntity> dependencies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<VersionEntity> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<VersionEntity> dependencies) {
        this.dependencies = dependencies;
    }
}
