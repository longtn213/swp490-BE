package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ConfigData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigDataRepository extends JpaRepository<ConfigData, Long>, JpaSpecificationExecutor<ConfigData> {
    @Query("select c from ConfigData c " +
        "join c.branch b where c.configKey = :key " +
        "and b.id = :branchId")
    Optional<ConfigData> findByConfigKeyAndBranch(@Param("key") String key, @Param("branchId") Long branchId);

    Optional<ConfigData> findByConfigKey(String key);

    List<ConfigData> findAllByBranchCode(String skinWisdomYenLang);
}
