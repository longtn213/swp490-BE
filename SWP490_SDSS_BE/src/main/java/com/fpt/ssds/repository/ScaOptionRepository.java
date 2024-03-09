package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScaOptionRepository extends JpaRepository<Option, Long> {
}
