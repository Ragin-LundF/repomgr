package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.common.MessageDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import com.repomgr.repomanager.rest.model.user.TokenDto;
import com.repomgr.repomanager.rest.model.user.UserDto;
import com.repomgr.repomanager.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling all authentication related requests.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/authentication")
public class RestAuthenticationController {
    private final static Logger LOG = LoggerFactory.getLogger(RestAuthenticationController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @Autowired
    public RestAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    /**
     * REST-API method for generating a new token for an user.
     *
     * @param userDto       Credentials of the user (username, password at the UserDto object)
     * @return              new token for the user
     * @throws AuthenticationException  error while authentication
     */
    @PostMapping(
            path = "/generate-token",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenDto> generateToken(@RequestBody UserDto userDto) {
        ResponseEntity<ResponseDto> response;
        LOG.debug("[RestAuthenticationController][generateToken] Generate token request accepted.");

        TokenDto tokenDto = new TokenDto();
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.getUsername(),
                            userDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final UserDto user = userService.lookupUser(userDto);
            final String token = jwtTokenUtil.generateToken(user);
            tokenDto = new TokenDto(token, userDto.getUserId());
            tokenDto.setStatus(true);
        } catch (AuthenticationException ae) {
            MessageDto messageDto = new MessageDto();
            messageDto.setCategory(Constants.REST_MESSAGE_CODE_ERROR);
            messageDto.setMessage("Unable to create token. Wrong credentials.");

            tokenDto = new TokenDto();
            tokenDto.setStatus(false);
            tokenDto.setMessage(messageDto);
            LOG.warn("[RestAuthenticationController][generateToken] Can not create token. Wrong credentials given.");
        }

        LOG.debug("[RestAuthenticationController][generateToken] Generate token request finished.");
        if (tokenDto.isStatus()) {
            return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(tokenDto, HttpStatus.UNAUTHORIZED);
        }
    }
}
