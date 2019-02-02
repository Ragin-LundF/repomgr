package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.MessageDto;
import com.repomgr.repomanager.rest.model.PasswordDto;
import com.repomgr.repomanager.rest.model.ResponseDto;
import com.repomgr.repomanager.rest.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @PostMapping
    public ResponseEntity storeUser(@RequestBody UserDto userDto) {
        if (userDto != null && StringUtils.isEmpty(userDto.getRole())) {
            return new ResponseEntity<>(new ResponseDto(false, new MessageDto("ERROR", "User empty or missing role.")), HttpStatus.BAD_REQUEST);
        } else {
            UserDto storedUser = userService.storeUser(userDto);

            return new ResponseEntity<>(storedUser, HttpStatus.CREATED);
        }
    }

    /**
     * REST-API method for updating the password of the user.
     *
     * @param userId            The userId (UUID)
     * @param passwordDto       Dto which contains the new password
     * @return                  updated user info (userId and valid) or ResponseDto message for error handling
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{userId}/password")
    public ResponseEntity updateUserPassword(@PathVariable("userId") String userId, @RequestBody PasswordDto passwordDto) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPassword(passwordDto.getPassword());
        UserDto updatedUser = userService.updatePassword(userDto);

        if (updatedUser != null && updatedUser.isValid() && ! StringUtils.isEmpty(updatedUser.getUserId())) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ResponseDto(false, new MessageDto("ERROR", "Can not update the user.")), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * REST-API method for deleting an user
     *
     * @param userId        The userId (UUID) to identify the user
     * @return              No Content (success) or BadRequest ResponseEntity
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") String userId) {
        if (userService.deleteByUserId(userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
