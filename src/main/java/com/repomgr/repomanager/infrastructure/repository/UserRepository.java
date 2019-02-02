package com.repomgr.repomanager.infrastructure.repository;

import com.repomgr.repomanager.infrastructure.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findFirstByUsername(String username);
    UserEntity findUserEntityByUserId(String userId);
}
