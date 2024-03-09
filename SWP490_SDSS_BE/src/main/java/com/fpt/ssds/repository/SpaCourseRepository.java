package com.fpt.ssds.repository;

import com.fpt.ssds.domain.SpaCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaCourseRepository extends JpaRepository<SpaCourse, Long> {
}
