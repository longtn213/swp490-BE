package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByCode(String code);

    Optional<Branch> findByLatitudeAndLongitude(Double latitude, Double longitude);

    List<Branch> findAllByIsActive(Boolean isActive);

    Optional<Branch> findByUsersId(Long userId);
}
