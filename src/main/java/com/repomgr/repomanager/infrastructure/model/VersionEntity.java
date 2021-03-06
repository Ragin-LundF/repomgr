package com.repomgr.repomanager.infrastructure.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

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
    @Column(name = "UID", nullable = false)
    private String uid;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "REPO_DEPENDENCY_JOIN",
            joinColumns = @JoinColumn(name = "ARTIFACT_ID"),
            inverseJoinColumns = @JoinColumn(name = "DEPENDENCY_ID")
    )
    private List<VersionEntity> dependencies;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
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

    public List<VersionEntity> getDependencies() {
        return dependencies;
    }

    public void setDependencies(final List<VersionEntity> dependencies) {
        this.dependencies = dependencies;
    }
}
