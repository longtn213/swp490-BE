package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ScaQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SCAQuestionRepository extends JpaRepository<ScaQuestion, Long>, JpaSpecificationExecutor<ScaQuestion> {
}
