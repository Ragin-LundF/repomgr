package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.infrastructure.repository.VersionRepository;
import com.repomgr.repomanager.rest.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling version and artifact information.
 */
@Service
public class VersionService {
    private final static Logger LOG = LoggerFactory.getLogger(VersionService.class);
    private final VersionRepository versionRepository;

    @Autowired
    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    /**
     * Store new version to database.
     *
     * @param versionInformationDto Version information DTO
     * @return ResponseDto with status and messages, if something failed.
     */
    public ResponseDto pushNewVersion(VersionInformationDto versionInformationDto) {
        ResponseDto responseDto = new ResponseDto(false);
        VersionEntity versionEntity = new VersionEntity();
        BeanUtils.copyProperties(versionInformationDto, versionEntity);

        VersionEntity savedEntity = versionRepository.save(versionEntity);

        if (savedEntity != null && savedEntity.getId() != null) {
            responseDto.setStatus(true);
        } else {
            String message = "Unable to store GroupId [" + versionInformationDto.getGroupId() + "] ArtifactId [" + versionInformationDto.getArtifactId() + "] in version [" + versionInformationDto.getVersion() + "]";
            MessageDto messageDto = new MessageDto(Constants.REST_MESSAGE_CODE_ERROR, message);
            responseDto.setMessage(messageDto);
            LOG.error(message);
        }
        return responseDto;
    }

    public VersionInformationContainerDto listVersionInformations(VersionInformationDto versionInformationDto, Pageable pageable) {
        // create query and execute database statement
        Page<VersionEntity> pagedResult = versionRepository.findAll((Specification<VersionEntity>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(versionInformationDto.getVersion())) {
                predicates.add(criteriaBuilder.equal(root.get("version"), versionInformationDto.getVersion()));
            }

            if (!StringUtils.isEmpty(versionInformationDto.getArtifactId())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artifactId")),
                        "%" + versionInformationDto.getArtifactId().toLowerCase() + "%"));
            }

            if (!StringUtils.isEmpty(versionInformationDto.getGroupId())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("groupId")),
                        "%" + versionInformationDto.getGroupId().toLowerCase() + "%"));
            }

            if (!StringUtils.isEmpty(versionInformationDto.getBranch())) {
                predicates.add(criteriaBuilder.equal(root.get("branch"), versionInformationDto.getBranch()));
            }

            if (versionInformationDto.getLatestVersion() != null && versionInformationDto.getLatestVersion()) {
                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<VersionEntity> from = subQuery.from(VersionEntity.class);
                subQuery.select(criteriaBuilder.max(from.get("id")).as(Long.class))
                        .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                        .groupBy(from.get("artifactId"), from.get("groupId"))
                        .from(VersionEntity.class);

                return criteriaBuilder.in(root.get("id")).value(subQuery);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        // map data to DTO
        List<VersionInformationDto> versionList = new ArrayList<>();
        if (!pagedResult.isEmpty()) {
            VersionInformationDto version;
            for (VersionEntity versionEntity : pagedResult.getContent()) {
                version = new VersionInformationDto();
                BeanUtils.copyProperties(versionEntity, version);
                versionList.add(version);
            }
        }

        // map pages
        PageDto pageDto = new PageDto();
        pageDto.setTotalElements(pagedResult.getTotalElements());
        pageDto.setTotalPages(pagedResult.getTotalPages());
        pageDto.setCurrentPage(1 + pagedResult.getNumber());
        pageDto.setNumberOfElements(pagedResult.getNumberOfElements());

        // map versioninformations
        VersionInformationContainerDto versionInformationContainerDto = new VersionInformationContainerDto();
        versionInformationContainerDto.setVersionInformations(versionList);
        versionInformationContainerDto.setPage(pageDto);

        return versionInformationContainerDto;
    }
}
