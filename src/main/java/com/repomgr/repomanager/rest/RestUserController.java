package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.common.MessageDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import com.repomgr.repomanager.rest.model.user.PasswordDto;
import com.repomgr.repomanager.rest.model.user.UserDto;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for handling all user related requests.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/users")
public class RestUserController {
    private final static Logger LOG = LoggerFactory.getLogger(RestUserController.class);
    private final UserService userService;

    @Autowired
    public RestUserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * REST-API method for storing user.
     *
     * @param userDto       UserDto object
     * @return              ResponseEntity with user information like userId if successful. Else it returns a ResponseDto message.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> storeUser(@RequestBody final UserDto userDto) {
        LOG.debug("[RestUserController][storeUser] Store user request accepted.");
        ResponseEntity<Object> response;
        if (userDto != null && ObjectUtils.isEmpty(userDto.getRole())) {
            response = new ResponseEntity<>(
                    new ResponseDto(false, new MessageDto("ERROR", "User empty or missing role.")),
                    HttpStatus.BAD_REQUEST
            );
        } else if (userDto != null) {
            final var storedUser = userService.storeUser(userDto);
            response = new ResponseEntity<>(storedUser, HttpStatus.CREATED);
        } else {
            response = ResponseEntity.badRequest().build();
        }

        LOG.debug("[RestUserController][storeUser] Store user request finished.");
        return response;
    }

    /**
     * REST-API method for updating the password of the user.
     *
     * @param aUserId            The userId (UUID)
     * @param passwordDto       Dto which contains the new password
     * @return                  updated user info (userId and valid) or ResponseDto message for error handling
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(
            path = "/{userId}/password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updateUserPassword(@PathVariable("userId") final String aUserId,
                                             @RequestBody final PasswordDto passwordDto
    ) {
        LOG.debug("[RestUserController][updateUserPassword] Update user password request accepted.");
        ResponseEntity<Object> response;

        final var userId = Encode.forJava(aUserId);
        final var userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPassword(passwordDto.getPassword());
        final var updatedUser = userService.updatePassword(userDto);

        if (updatedUser != null && updatedUser.isValid() && ! ObjectUtils.isEmpty(updatedUser.getUserId())) {
            response = new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new ResponseDto(
                    false,
                    new MessageDto("ERROR", "Can not update the user.")
            ), HttpStatus.BAD_REQUEST);
        }

        LOG.debug("[RestUserController][updateUserPassword] Update user password request finished.");
        return response;
    }

    /**
     * REST-API method for deleting an user
     *
     * @param userId        The userId (UUID) to identify the user
     * @return              No Content (success) or BadRequest ResponseEntity
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(
            path = "/{userId}"
    )
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") final String userId) {
        LOG.debug("[RestUserController][deleteUser] Delete user request accepted.");
        ResponseEntity<Object> response;

        if (userService.deleteByUserId(userId)) {
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.badRequest().build();
        }

        LOG.debug("[RestUserController][deleteUser] Delete user request finished.");
        return response;
    }
}
