package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.infrastructure.model.UserEntity;
import com.repomgr.repomanager.infrastructure.repository.UserRepository;
import com.repomgr.repomanager.rest.model.user.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
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
        LOG.debug("[UserService][lookupUser] Lookup user in service started.");

        UserEntity userEntity = userRepository.findFirstByUsername(userDto.getUsername());
        if (! StringUtils.isEmpty(userEntity.getUsername()) && ! StringUtils.isEmpty(userEntity.getId())) {
            BeanUtils.copyProperties(userEntity, userDto);
            userDto.setValid(true);
        } else {
            userDto.setValid(false);
        }

        LOG.debug("[UserService][lookupUser] Lookup user in service finished.");
        return userDto;
    }

    /**
     * Store a new user to the database
     *
     * @param userDto   filled userDto object
     * @return          userDto object with new userId.
     */
    public UserDto storeUser(UserDto userDto) {
        LOG.debug("[UserService][storeUser] Store user in service started.");
        UserEntity dbUser = userRepository.findFirstByUsername(userDto.getUsername());
        if (dbUser == null) {
            UserEntity userEntity = new UserEntity();
            BeanUtils.copyProperties(userDto, userEntity);
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
            userEntity.setUserId(UUID.randomUUID().toString());
            userEntity = userRepository.save(userEntity);

            if (!StringUtils.isEmpty(userEntity.getId())) {
                userDto = new UserDto(true, userEntity.getUserId());
            }
        } else {
            userDto.setValid(false);
        }

        LOG.debug("[UserService][storeUser] Store user in service finished.");
        return userDto;
    }

    /**
     * Delete an user with the userId
     *
     * @param userId        userId of an user (UUID)
     * @return              true if successful, false if failed.
     */
    public boolean deleteByUserId(String userId) {
        LOG.debug("[UserService][deleteByUserId] Delete user in service started.");
        boolean successful = false;
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
        if (userEntity != null && ! StringUtils.isEmpty(userEntity.getId())) {
            userRepository.deleteById(userEntity.getId());
            successful = true;
        }

        LOG.debug("[UserService][deleteByUserId] Delete user in service finished.");
        return successful;
    }

    /**
     * Lookup for granted authorities for a role
     *
     * @param role  Role
     * @return      List of granted authorities
     */
    public List<SimpleGrantedAuthority> lookupAuthority(String role) {
        LOG.debug("[UserService][lookupAuthority] Lookup authority in service executed.");
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    /**
     * Update the password of an user,
     *
     * @param userDto   UserDto with userId and new password.
     * @return          UserDto with userId and valid state if successful
     */
    public UserDto updatePassword(UserDto userDto) {
        LOG.debug("[UserService][updatePassword] Update password in service started.");
        UserEntity userEntity = userRepository.findUserEntityByUserId(userDto.getUserId());
        if(userEntity != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            UserEntity updatedUser = userRepository.save(userEntity);
            if (! StringUtils.isEmpty(updatedUser.getId())) {
                userDto = new UserDto(true, updatedUser.getUserId());
            }
        } else {
            userDto.setValid(false);
        }

        LOG.debug("[UserService][updatePassword] Update password in service finished.");
        return userDto;
    }

    /**
     * Overwritten method from Spring Security to load an user.
     *
     * @param username          username
     * @return                  Spring UserDetails object
     * @throws UsernameNotFoundException no username was found
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        LOG.debug("[UserService][loadUserByUsername] Lookup user by username in service started.");
        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto = lookupUser(userDto);

        if (userDto == null) {
            LOG.debug("[UserService][loadUserByUsername] Lookup user by username in service failed.");
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        LOG.debug("[UserService][loadUserByUsername] Lookup user by username in service finished.");
        return new User(userDto.getUsername(), userDto.getPassword(), lookupAuthority(userDto.getRole()));
    }
}
