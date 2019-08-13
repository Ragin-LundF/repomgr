package com.repomgr.repomanager.rest.model.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents an artifact maven like with a group and a artifactId/name
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactDto {
    @NotNull
    @Size(min = 1, max = 100)
    private String groupId;
    @NotNull
    @Size(min = 1, max = 100)
    private String artifactId;
    @NotNull
    @Size(min = 1, max = 20)
    private String version;
    @Size(max = 55)
    private String uid;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
