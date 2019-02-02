package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.VersionService;
import com.repomgr.repomanager.rest.model.ResponseDto;
import com.repomgr.repomanager.rest.model.VersionInformationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class RestRepositoryController {
    private final VersionService versionService;

    @Autowired
    public RestRepositoryController(VersionService versionService) {
        this.versionService = versionService;
    }

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
