package com.repomgr.repomanager.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionInformationContainer {
    private List<VersionInformationDto> versionInformations;

    public List<VersionInformationDto> getVersionInformations() {
        return versionInformations;
    }

    public void setVersionInformations(List<VersionInformationDto> versionInformations) {
        this.versionInformations = versionInformations;
    }
}
