package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.infrastructure.repository.VersionRepository;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationContainerDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationDto;
import com.repomgr.repomanager.rest.model.common.MessageDto;
import com.repomgr.repomanager.rest.model.common.PageDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling version and artifact information.
 */
@Service
public class VersionService {
    private static final Logger LOG = LoggerFactory.getLogger(VersionService.class);
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
        LOG.debug("[VersionService][pushNewVersion] Push new version in service started.");
        ResponseDto responseDto = new ResponseDto(false);
        VersionEntity versionEntity = new VersionEntity();
        BeanUtils.copyProperties(versionInformationDto, versionEntity);
        BeanUtils.copyProperties(versionInformationDto.getArtifact(), versionEntity);

        VersionEntity savedEntity = versionRepository.save(versionEntity);

        if (savedEntity.getId() != null) {
            responseDto.setStatus(true);
        } else {
            String message = "Unable to store GroupId [" + versionInformationDto.getArtifact().getGroupId() + "] ArtifactId [" + versionInformationDto.getArtifact().getArtifactId() + "] in version [" + versionInformationDto.getArtifact().getVersion() + "]";
            MessageDto messageDto = new MessageDto(Constants.REST_MESSAGE_CODE_ERROR, message);
            responseDto.setMessage(messageDto);
            LOG.error(message);
        }

        LOG.debug("[VersionService][pushNewVersion] Push new version in service finished.");
        return responseDto;
    }

    /**
     * List information of artifacts by version information filter.
     *
     * @param versionInformationDto     filter data
     * @param pageable                  paging information
     * @return                          list of artifacts
     */
    public VersionInformationContainerDto listVersionInformation(VersionInformationDto versionInformationDto, Pageable pageable) {
        LOG.debug("[VersionService][listVersionInformation] List versions in service started.");

        // create query and execute database statement
        Page<VersionEntity> pagedResult = versionRepository.findAll((Specification<VersionEntity>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(versionInformationDto.getArtifact().getVersion())) {
                predicates.add(criteriaBuilder.equal(root.get("version"), versionInformationDto.getArtifact().getVersion()));
            }

            if (!StringUtils.isEmpty(versionInformationDto.getArtifact().getArtifactId())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artifactId")),
                        "%" + versionInformationDto.getArtifact().getArtifactId().toLowerCase() + "%"));
            }

            if (!StringUtils.isEmpty(versionInformationDto.getArtifact().getGroupId())) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("groupId")),
                        "%" + versionInformationDto.getArtifact().getGroupId().toLowerCase() + "%"));
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

        // map version information
        VersionInformationContainerDto versionInformationContainerDto = new VersionInformationContainerDto();
        versionInformationContainerDto.setVersionInformations(versionList);
        versionInformationContainerDto.setPage(pageDto);

        LOG.debug("[VersionService][listVersionInformation] List versions in service finished.");
        return versionInformationContainerDto;
    }
}
