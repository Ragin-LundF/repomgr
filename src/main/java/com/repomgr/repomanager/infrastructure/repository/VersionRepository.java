package com.repomgr.repomanager.infrastructure.repository;

import com.repomgr.repomanager.infrastructure.model.VersionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends CrudRepository<VersionEntity, Long> {
}
