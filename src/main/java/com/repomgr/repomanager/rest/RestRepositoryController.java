package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.VersionService;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationContainerDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import javax.validation.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling all repositories related requests.
 */
@RestController
@RequestMapping("/v1/repositories")
public class RestRepositoryController {
    private final static Logger LOG = LoggerFactory.getLogger(RestRepositoryController.class);
    private final VersionService versionService;

    @Autowired
    public RestRepositoryController(final VersionService versionService) {
        this.versionService = versionService;
    }

    /**
     * REST-API method for pushing new version information to the database.
     *
     * @param versionInformationDto     Information about the artifact
     * @return                          Created (success) or Bad Request (error)
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ResponseDto> pushNewVersion(final @RequestBody VersionInformationDto versionInformationDto) {
        ResponseEntity<ResponseDto> response;
        LOG.debug("[RestRepositoryController][pushNewVersion] Push new version request accepted.");

        final var responseDto = versionService.pushNewVersion(versionInformationDto);

        if (responseDto.isStatus()) {
            response = new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } else {
            response = new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }

        LOG.debug("[RestRepositoryController][pushNewVersion] Push new version request finished.");
        return response;
    }

    @PostMapping(
            path = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VersionInformationContainerDto> listVersions(
            @RequestBody final VersionInformationDto versionInformationDto,
            @Nullable @RequestParam final String sortField,
            @Nullable @RequestParam final Sort.Direction sortDirection,
            @Nullable @RequestParam final Integer aPage,
            @Nullable @Max(100) @RequestParam final Integer aSize
    ) {
        LOG.debug("[RestRepositoryController][listVersions] List versions request accepted.");

        // Paging
        final var size = (aSize == null) ? 10 : aSize;
        final var page = (aPage == null) ? 0 : aPage;
        Pageable pageable;
        if (!ObjectUtils.isEmpty(sortField) && ! ObjectUtils.isEmpty(sortDirection)) {
            pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        // get data and return
        final var versionInformationContainer = versionService.listVersionInformation(versionInformationDto, pageable);

        LOG.debug("[RestRepositoryController][listVersions] List versions request finished.");
        return new ResponseEntity<>(versionInformationContainer, HttpStatus.OK);
    }
}
