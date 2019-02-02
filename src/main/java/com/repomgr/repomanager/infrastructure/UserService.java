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
import java.util.Objects;
import java.util.UUID;

@Service(value = "userService")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

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

    public UserDto storeUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(userDto, userEntity);
        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity = userRepository.save(userEntity);

        if (! StringUtils.isEmpty(userEntity.getId())) {
            return new UserDto(true, userEntity.getUserId());
        }

        return userDto;
    }

    public boolean deleteByUserId(String userId) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
        if (userEntity != null && ! StringUtils.isEmpty(userEntity.getId())) {
            userRepository.deleteById(userEntity.getId());
            return true;
        }
        return false;
    }

    public List<SimpleGrantedAuthority> lookupAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    public UserDto updatePassword(UserDto userDto) {
        UserEntity userEntity = userRepository.findUserEntityByUserId(userDto.getUserId());
        if(userEntity != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            UserEntity updatedUser = userRepository.save(userEntity);
            if (updatedUser != null && ! StringUtils.isEmpty(updatedUser.getId())) {
                return new UserDto(true, updatedUser.getUserId());
            }
        }
        return userDto;
    }

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
