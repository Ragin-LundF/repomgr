package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.MessageDto;
import com.repomgr.repomanager.rest.model.PasswordDto;
import com.repomgr.repomanager.rest.model.ResponseDto;
import com.repomgr.repomanager.rest.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class RestUserController {
    private final UserService userService;

    @Autowired
    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity storeUser(@RequestBody UserDto userDto) {
        if (userDto != null && StringUtils.isEmpty(userDto.getRole())) {
            return new ResponseEntity<>(new ResponseDto(false, new MessageDto("ERROR", "User empty or missing role.")), HttpStatus.BAD_REQUEST);
        } else {
            UserDto storedUser = userService.storeUser(userDto);

            return new ResponseEntity<>(storedUser, HttpStatus.CREATED);
        }
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<UserDto> updateUserPassword(@PathVariable("userId") String userId, @RequestBody PasswordDto passwordDto) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setPassword(passwordDto.getPassword());
        UserDto updatedUser = userService.updatePassword(userDto);

        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") String userId) {
        if (userService.deleteByUserId(userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
