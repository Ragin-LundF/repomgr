package com.repomgr.repomanager.rest;

import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.user.TokenDto;
import com.repomgr.repomanager.rest.model.user.UserDto;
import com.repomgr.repomanager.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * @throws AuthenticationException
     */
    @PostMapping(value = "/generate-token")
    public ResponseEntity<TokenDto> generateToken(@RequestBody UserDto userDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDto user = userService.lookupUser(userDto);
        final String token = jwtTokenUtil.generateToken(user);

        return new ResponseEntity<>(new TokenDto(token, userDto.getUserId()), HttpStatus.CREATED);
    }
}
