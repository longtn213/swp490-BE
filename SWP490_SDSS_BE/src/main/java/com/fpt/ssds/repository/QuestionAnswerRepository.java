package com.fpt.ssds.repository;

import com.fpt.ssds.domain.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long>, JpaSpecificationExecutor<QuestionAnswer> {
}
