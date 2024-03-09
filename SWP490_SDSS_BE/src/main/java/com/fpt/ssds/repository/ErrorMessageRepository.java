package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ErrorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
    Optional<ErrorMessage> findByCode(String code);
}
