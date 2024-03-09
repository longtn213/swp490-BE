package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ScaForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SCAFormRepository extends JpaRepository<ScaForm, Long>, JpaSpecificationExecutor<ScaForm> {
    Optional<ScaForm> findByCode(String code);

}
