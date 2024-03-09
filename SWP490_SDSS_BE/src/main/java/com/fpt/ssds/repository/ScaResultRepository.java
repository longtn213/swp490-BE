package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ScaResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScaResultRepository extends JpaRepository<ScaResult, Long>, JpaSpecificationExecutor<ScaResult> {

    List<ScaResult> findByCustomerId(@Param("customerId") Long id);
}
