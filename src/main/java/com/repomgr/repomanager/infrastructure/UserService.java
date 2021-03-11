package com.repomgr.repomanager.infrastructure;

import com.repomgr.repomanager.infrastructure.model.UserEntity;
import com.repomgr.repomanager.infrastructure.repository.UserRepository;
import com.repomgr.repomanager.rest.model.user.UserDto;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
    public UserService(final UserRepository userRepository,
                       final BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
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
    public UserDto lookupUser(final UserDto userDto) {
        LOG.debug("[UserService][lookupUser] Lookup user in service started.");

        final var userEntity = userRepository.findFirstByUsername(userDto.getUsername());
        if (! ObjectUtils.isEmpty(userEntity.getUsername()) && ! ObjectUtils.isEmpty(userEntity.getId())) {
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
    @Transactional
    public UserDto storeUser(final UserDto userDto) {
        LOG.debug("[UserService][storeUser] Store user in service started.");
        final var dbUser = userRepository.findFirstByUsername(userDto.getUsername());
        if (dbUser == null) {
            var userEntity = new UserEntity();
            BeanUtils.copyProperties(userDto, userEntity);
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
            userEntity.setUserId(UUID.randomUUID().toString());
            userEntity = userRepository.save(userEntity);

            if (!ObjectUtils.isEmpty(userEntity.getId())) {
                userDto.setValid(true);
                userDto.setUserId(userEntity.getUserId());
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
    @Transactional
    public boolean deleteByUserId(final String userId) {
        LOG.debug("[UserService][deleteByUserId] Delete user in service started.");
        var successful = false;
        final var userEntity = userRepository.findUserEntityByUserId(userId);
        if (userEntity != null && ! ObjectUtils.isEmpty(userEntity.getId())) {
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
    public List<SimpleGrantedAuthority> lookupAuthority(final String role) {
        LOG.debug("[UserService][lookupAuthority] Lookup authority in service executed.");
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    /**
     * Update the password of an user,
     *
     * @param userDto   UserDto with userId and new password.
     * @return          UserDto with userId and valid state if successful
     */
    @Transactional
    public UserDto updatePassword(final UserDto userDto) {
        LOG.debug("[UserService][updatePassword] Update password in service started.");
        final var userEntity = userRepository.findUserEntityByUserId(userDto.getUserId());
        if (userEntity != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            final var updatedUser = userRepository.save(userEntity);
            if (! ObjectUtils.isEmpty(updatedUser.getId())) {
                userDto.setValid(true);
                userDto.setUserId(updatedUser.getUserId());
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
    public UserDetails loadUserByUsername(final String username) {
        LOG.debug("[UserService][loadUserByUsername] Lookup user by username in service started.");
        var userDto = new UserDto();
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
