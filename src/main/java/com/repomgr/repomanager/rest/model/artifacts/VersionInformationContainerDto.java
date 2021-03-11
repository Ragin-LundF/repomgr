package com.repomgr.repomanager.rest.model.artifacts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repomgr.repomanager.rest.model.common.PageDto;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VersionInformationContainerDto {
    private List<VersionInformationDto> versionInformation;
    private PageDto page;

    public List<VersionInformationDto> getVersionInformation() {
        return versionInformation;
    }

    public void setVersionInformation(final List<VersionInformationDto> versionInformation) {
        this.versionInformation = versionInformation;
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(final PageDto page) {
        this.page = page;
    }
}
