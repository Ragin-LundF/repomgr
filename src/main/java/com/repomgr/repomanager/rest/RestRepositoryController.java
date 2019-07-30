package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.VersionService;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationContainerDto;
import com.repomgr.repomanager.rest.model.artifacts.VersionInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;

/**
 * REST Controller for handling all repositories related requests.
 */
@RestController
@RequestMapping("/v1/repositories")
public class RestRepositoryController {
    private final VersionService versionService;

    @Autowired
    public RestRepositoryController(VersionService versionService) {
        this.versionService = versionService;
    }

    /**
     * REST-API method for pushing new version information to the database.
     *
     * @param versionInformationDto     Information about the artifact
     * @return                          Created (success) or Bad Request (error)
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping
    public ResponseEntity<ResponseDto> pushNewVersion(@RequestBody VersionInformationDto versionInformationDto) {
        ResponseDto responseDto = versionService.pushNewVersion(versionInformationDto);

        if (responseDto.isStatus()) {
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<VersionInformationContainerDto> listVersions(
            @RequestBody VersionInformationDto versionInformationDto,
            @Nullable @RequestParam String sortField,
            @Nullable @RequestParam Sort.Direction sortDirection,
            @Nullable @RequestParam Integer page,
            @Nullable @Max(100) @RequestParam Integer size
    ) {
        // Paging
        size = (size == null) ? 10 : size;
        page = (page == null) ? 0 : page;
        Pageable pageable;
        if (!StringUtils.isEmpty(sortField) && ! StringUtils.isEmpty(sortDirection)) {
            pageable = PageRequest.of(page, size, new Sort(sortDirection, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        // get data and return
        VersionInformationContainerDto versionInformationContainer = versionService.listVersionInformations(versionInformationDto, pageable);

        return new ResponseEntity<>(versionInformationContainer, HttpStatus.OK);
    }
}
