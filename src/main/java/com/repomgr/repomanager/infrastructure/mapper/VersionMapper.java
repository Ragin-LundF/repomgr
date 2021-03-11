package com.repomgr.repomanager.infrastructure.mapper;

import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.rest.model.artifacts.ArtifactDto;
import com.repomgr.repomanager.rest.model.common.PageDto;
import java.util.ArrayList;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class VersionMapper {
    public ArtifactDto fromVersionEntity(final VersionEntity versionEntity) {
        final var artifactDto = new ArtifactDto();
        artifactDto.setVersion(versionEntity.getVersion());
        artifactDto.setGroupId(versionEntity.getGroupId());
        artifactDto.setArtifactId(versionEntity.getArtifactId());

        return artifactDto;
    }

    public ArrayList<ArtifactDto> depsFromVersionEntities(final VersionEntity versionEntity) {
        final var artifactDtoArrayList = new ArrayList<ArtifactDto>();
        for (final var versionDependencyEntity : versionEntity.getDependencies()) {
            final var depArtifactDto = new ArtifactDto();
            BeanUtils.copyProperties(versionDependencyEntity, depArtifactDto);
            artifactDtoArrayList.add(depArtifactDto);
        }

        return artifactDtoArrayList;
    }

    public PageDto fromPageVersionEntity(final Page<VersionEntity> pagedResult) {
        final var pageDto = new PageDto();
        pageDto.setTotalElements(pagedResult.getTotalElements());
        pageDto.setTotalPages(pagedResult.getTotalPages());
        pageDto.setCurrentPage(1 + pagedResult.getNumber());
        pageDto.setNumberOfElements(pagedResult.getNumberOfElements());

        return pageDto;
    }
}
