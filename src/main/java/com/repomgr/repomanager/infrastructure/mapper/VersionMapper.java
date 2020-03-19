package com.repomgr.repomanager.infrastructure.mapper;

import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.rest.model.artifacts.ArtifactDto;
import com.repomgr.repomanager.rest.model.common.PageDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class VersionMapper {
    public ArtifactDto fromVersionEntity(VersionEntity versionEntity) {
        ArtifactDto artifactDto = new ArtifactDto();
        artifactDto.setVersion(versionEntity.getVersion());
        artifactDto.setGroupId(versionEntity.getGroupId());
        artifactDto.setArtifactId(versionEntity.getArtifactId());

        return artifactDto;
    }

    public ArrayList<ArtifactDto> depsFromVersionEntities(VersionEntity versionEntity) {
        ArrayList<ArtifactDto> artifactDtoArrayList = new ArrayList<>();
        for (VersionEntity versionDependencyEntity : versionEntity.getDependencies()) {
            ArtifactDto depArtifactDto = new ArtifactDto();
            BeanUtils.copyProperties(versionDependencyEntity, depArtifactDto);
            artifactDtoArrayList.add(depArtifactDto);
        }

        return artifactDtoArrayList;
    }

    public PageDto fromPageVersionEntity(Page<VersionEntity> pagedResult) {
        PageDto pageDto = new PageDto();
        pageDto.setTotalElements(pagedResult.getTotalElements());
        pageDto.setTotalPages(pagedResult.getTotalPages());
        pageDto.setCurrentPage(1 + pagedResult.getNumber());
        pageDto.setNumberOfElements(pagedResult.getNumberOfElements());

        return pageDto;
    }
}
