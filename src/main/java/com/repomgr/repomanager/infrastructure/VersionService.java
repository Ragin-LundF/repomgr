package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import com.repomgr.repomanager.infrastructure.repository.VersionRepository;
import com.repomgr.repomanager.rest.model.MessageDto;
import com.repomgr.repomanager.rest.model.ResponseDto;
import com.repomgr.repomanager.rest.model.VersionInformationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param versionInformationDto     Version information DTO
     * @return                          ResponseDto with status and messages, if something failed.
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
}
