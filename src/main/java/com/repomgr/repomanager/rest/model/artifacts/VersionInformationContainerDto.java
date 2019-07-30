package com.repomgr.repomanager.rest.model.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repomgr.repomanager.rest.model.common.PageDto;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionInformationContainerDto {
    private List<VersionInformationDto> versionInformations;
    private PageDto page;

    public List<VersionInformationDto> getVersionInformations() {
        return versionInformations;
    }

    public void setVersionInformations(List<VersionInformationDto> versionInformations) {
        this.versionInformations = versionInformations;
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }
}
