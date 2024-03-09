package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ReasonMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonMappingRepository extends JpaRepository<ReasonMapping, Long> {
}
