package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.infrastructure.model.UserEntity;
import com.repomgr.repomanager.infrastructure.repository.UserRepository;
import com.repomgr.repomanager.rest.model.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * User service for handling user related actions.
 * <br />
 * This service also implements also the UserDetailsService for SpringSecurity.
 *
 * @see UserDetailsService
 */
@Service(value = "userService")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Lookup for an user.
     * <br />
     * If an user was found, the filled UserDto object will be returned.
     *
     * @param userDto   userDto object with filled username field.
     * @return          filled userDto from database
     */
    public UserDto lookupUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findFirstByUsername(userDto.getUsername());
        if (! StringUtils.isEmpty(userEntity.getUsername()) && ! StringUtils.isEmpty(userEntity.getId())) {
            BeanUtils.copyProperties(userEntity, userDto);
            userDto.setValid(true);
            return userDto;
        }
        userDto.setValid(false);

        return userDto;
    }

    /**
     * Store a new user to the database
     *
     * @param userDto   filled userDto object
     * @return          userDto object with new userId.
     */
    public UserDto storeUser(UserDto userDto) {
        UserEntity dbUser = userRepository.findFirstByUsername(userDto.getUsername());
        if (dbUser == null) {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(userDto, userEntity);
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
            userEntity.setUserId(UUID.randomUUID().toString());
            userEntity = userRepository.save(userEntity);

            if (!StringUtils.isEmpty(userEntity.getId())) {
                return new UserDto(true, userEntity.getUserId());
            }
        }

        userDto.setValid(false);

        return userDto;
    }

    /**
     * Delete an user with the userId
     *
     * @param userId        userId of an user (UUID)
     * @return              true if successful, false if failed.
     */
    public boolean deleteByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
        if (userEntity != null && ! StringUtils.isEmpty(userEntity.getId())) {
            userRepository.deleteById(userEntity.getId());
            return true;
        }
        return false;
    }

    /**
     * Lookup for granted authorities for a role
     *
     * @param role  Role
     * @return      List of granted authorities
     */
    public List<SimpleGrantedAuthority> lookupAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    /**
     * Update the password of an user,
     *
     * @param userDto   UserDto with userId and new password.
     * @return          UserDto with userId and valid state if successful
     */
    public UserDto updatePassword(UserDto userDto) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userDto.getUserId());
        if(userEntity != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            UserEntity updatedUser = userRepository.save(userEntity);
            if (updatedUser != null && ! StringUtils.isEmpty(updatedUser.getId())) {
                return new UserDto(true, updatedUser.getUserId());
            }
        }
        userDto.setValid(false);
        return userDto;
    }

    /**
     * Overwriten method from Spring Security to load an user.
     *
     * @param username          username
     * @return                  Spring UserDetails object
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto = lookupUser(userDto);

        if (userDto == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new User(userDto.getUsername(), userDto.getPassword(), lookupAuthority(userDto.getRole()));
    }
}
