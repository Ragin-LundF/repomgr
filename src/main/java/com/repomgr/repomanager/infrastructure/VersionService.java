package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.mapper.VersionMapper;
import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.infrastructure.repository.VersionRepository;
import com.repomgr.repomanager.rest.model.artifacts.ArtifactDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationContainerDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationDto;
import com.repomgr.repomanager.rest.model.common.MessageDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import io.jsonwebtoken.lang.Collections;
import java.util.ArrayList;
import java.util.UUID;
import javax.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * Service class for handling version and artifact information.
 */
@Service
public class VersionService {
    private static final Logger LOG = LoggerFactory.getLogger(VersionService.class);
    private final VersionRepository versionRepository;
    private final VersionMapper versionMapper;

    @Autowired
    public VersionService(final VersionRepository versionRepository,
                          final VersionMapper versionMapper
    ) {
        this.versionRepository = versionRepository;
        this.versionMapper = versionMapper;
    }

    /**
     * Store new version to database.
     *
     * @param versionInformationDto Version information DTO
     * @return ResponseDto with status and messages, if something failed.
     */
    @Transactional
    public ResponseDto pushNewVersion(final VersionInformationDto versionInformationDto) {
        LOG.debug("[VersionService][pushNewVersion] Push new version in service started.");
        final var responseDto = new ResponseDto(false);
        final var versionEntity = new VersionEntity();
        BeanUtils.copyProperties(versionInformationDto, versionEntity);
        BeanUtils.copyProperties(versionInformationDto.getArtifact(), versionEntity);
        if (ObjectUtils.isEmpty(versionEntity.getUid())) {
            versionEntity.setUid(UUID.randomUUID().toString());
        }

        resolveDependencies(versionInformationDto, versionEntity);

        final var savedEntity = versionRepository.save(versionEntity);

        if (savedEntity.getId() != null) {
            responseDto.setStatus(true);
        } else {
            final var message = "Unable to store GroupId [" + versionInformationDto.getArtifact().getGroupId() + "] ArtifactId [" + versionInformationDto.getArtifact().getArtifactId() + "] in version [" + versionInformationDto.getArtifact().getVersion() + "]";
            final var messageDto = new MessageDto(Constants.REST_MESSAGE_CODE_ERROR, message);
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
    @Transactional
    public VersionInformationContainerDto listVersionInformation(final VersionInformationDto versionInformationDto,
                                                                 final Pageable pageable
    ) {
        LOG.debug("[VersionService][listVersionInformation] List versions in service started.");

        // create query and execute database statement
        final var pagedResult = listVersionEntities(versionInformationDto, pageable);

        // map data to DTO
        final var versionList = new ArrayList<VersionInformationDto>();
        if (!pagedResult.isEmpty()) {
            VersionInformationDto version;
            for (final var versionEntity : pagedResult.getContent()) {
                version = new VersionInformationDto();
                BeanUtils.copyProperties(versionEntity, version);
                // to be secure, that BeanUtils did not set any proxy database classes
                version.setDependencies(null);

                // Map artifact
                final var artifactDto = versionMapper.fromVersionEntity(versionEntity);
                version.setArtifact(artifactDto);

                // Map dependencies
                if (! Collections.isEmpty(versionEntity.getDependencies())) {
                    final var artifactDtoArrayList = versionMapper.depsFromVersionEntities(versionEntity);
                    version.setDependencies(artifactDtoArrayList);
                }
                versionList.add(version);
            }
        }

        // map pages
        final var pageDto = versionMapper.fromPageVersionEntity(pagedResult);

        // map version information
        final var versionInformationContainerDto = new VersionInformationContainerDto();
        versionInformationContainerDto.setVersionInformation(versionList);
        versionInformationContainerDto.setPage(pageDto);

        LOG.debug("[VersionService][listVersionInformation] List versions in service finished.");
        return versionInformationContainerDto;
    }

    /**
     * List version entities from database
     *
     * @param versionInformationDto VersionInformationDto filter
     * @param pageable              Paging
     * @return                      Database entity as page
     */
    @Transactional
    public Page<VersionEntity> listVersionEntities(final VersionInformationDto versionInformationDto,
                                                   final Pageable pageable
    ) {
        LOG.debug("[VersionService][listVersionEntities] List version entities in service started.");
        // create query and execute database statement
        final var pagedResult = versionRepository.findAll((Specification<VersionEntity>) (root, query, criteriaBuilder) -> {
            final var predicates = new ArrayList<Predicate>();
            if (versionInformationDto.getArtifact() != null) {
                if (!ObjectUtils.isEmpty(versionInformationDto.getArtifact().getVersion())) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("version"),
                            versionInformationDto.getArtifact().getVersion())
                    );
                }

                if (!ObjectUtils.isEmpty(versionInformationDto.getArtifact().getArtifactId())) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("artifactId")),
                            "%" + versionInformationDto.getArtifact().getArtifactId().toLowerCase() + "%"));
                }

                if (!ObjectUtils.isEmpty(versionInformationDto.getArtifact().getGroupId())) {
                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("groupId")),
                            "%" + versionInformationDto.getArtifact().getGroupId().toLowerCase() + "%"));
                }
            }

            if (!ObjectUtils.isEmpty(versionInformationDto.getBranch())) {
                predicates.add(criteriaBuilder.equal(root.get("branch"), versionInformationDto.getBranch()));
            }

            if (versionInformationDto.getLatestVersion() != null && versionInformationDto.getLatestVersion()) {
                final var subQuery = query.subquery(Long.class);
                final var from = subQuery.from(VersionEntity.class);
                subQuery.select(criteriaBuilder.max(from.get("id")).as(Long.class))
                        .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])))
                        .groupBy(from.get("artifactId"), from.get("groupId"))
                        .from(VersionEntity.class);

                return criteriaBuilder.in(root.get("id")).value(subQuery);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);

        LOG.debug("[VersionService][listVersionEntities] List version entities in service finished.");
        return pagedResult;
    }

    /**
     * Resolve dependencies by artifact, group and version.
     *
     * @param versionInformationDto     Request object
     * @param versionEntity             Version entity to store
     * @return                          version entity to store with dependencies
     */
    public VersionEntity resolveDependencies(final VersionInformationDto versionInformationDto,
                                             final VersionEntity versionEntity
    ) {
        LOG.debug("[VersionService][resolveDependencies] Resolve version dependencies service started.");
        if (versionInformationDto.getDependencies() != null) {
            versionEntity.setDependencies(new ArrayList<>());

            for (final var artifactEntry : versionInformationDto.getDependencies()) {
                final var artifactFilter = new ArtifactDto();
                artifactFilter.setArtifactId(artifactEntry.getArtifactId());
                artifactFilter.setGroupId(artifactEntry.getGroupId());
                artifactFilter.setVersion(artifactEntry.getVersion());

                final var filter = new VersionInformationDto();
                filter.setArtifact(artifactFilter);

                final var versionEntities = listVersionEntities(filter, Pageable.unpaged());
                if (versionEntities != null && versionEntities.hasContent()) {
                    VersionEntity dependencyVersionEntity = versionEntities.getContent().get(0);
                    versionEntity.getDependencies().add(dependencyVersionEntity);
                }
            }
        }

        LOG.debug("[VersionService][resolveDependencies] Resolve version dependencies service finished.");
        return versionEntity;
    }
}
