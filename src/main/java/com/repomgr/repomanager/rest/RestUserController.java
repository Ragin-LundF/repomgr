package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.common.MessageDto;
import com.repomgr.repomanager.rest.model.user.PasswordDto;
import com.repomgr.repomanager.rest.model.common.ResponseDto;
import com.repomgr.repomanager.rest.model.user.UserDto;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
    public RestUserController(UserService userService) {
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
    public ResponseEntity storeUser(@RequestBody UserDto userDto) {
        LOG.debug("[RestUserController][storeUser] Store user request accepted.");
        ResponseEntity response;
        if (userDto != null && StringUtils.isEmpty(userDto.getRole())) {
            response = new ResponseEntity<>(new ResponseDto(false, new MessageDto("ERROR", "User empty or missing role.")), HttpStatus.BAD_REQUEST);
        } else {
            UserDto storedUser = userService.storeUser(userDto);
            response = new ResponseEntity<>(storedUser, HttpStatus.CREATED);
        }

        LOG.debug("[RestUserController][storeUser] Store user request finished.");
        return response;
    }

    /**
     * REST-API method for updating the password of the user.
     *
     * @param userId            The userId (UUID)
     * @param passwordDto       Dto which contains the new password
     * @return                  updated user info (userId and valid) or ResponseDto message for error handling
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(
            path = "/{userId}/password",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity updateUserPassword(@PathVariable("userId") String userId, @RequestBody PasswordDto passwordDto) {
        LOG.debug("[RestUserController][updateUserPassword] Update user password request accepted.");
        ResponseEntity response;

        userId = Encode.forJava(userId);
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPassword(passwordDto.getPassword());
        UserDto updatedUser = userService.updatePassword(userDto);

        if (updatedUser != null && updatedUser.isValid() && ! StringUtils.isEmpty(updatedUser.getUserId())) {
            response = new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new ResponseDto(false, new MessageDto("ERROR", "Can not update the user.")), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity deleteUser(@PathVariable("userId") String userId) {
        LOG.debug("[RestUserController][deleteUser] Delete user request accepted.");
        ResponseEntity response;

        if (userService.deleteByUserId(userId)) {
            response = ResponseEntity.noContent().build();
        } else {
            response = ResponseEntity.badRequest().build();
        }

        LOG.debug("[RestUserController][deleteUser] Delete user request finished.");
        return response;
    }
}
