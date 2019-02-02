package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.VersionService;
import com.repomgr.repomanager.rest.model.ResponseDto;
import com.repomgr.repomanager.rest.model.VersionInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
