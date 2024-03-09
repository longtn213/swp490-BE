package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByDivisionParentIdIsNull();

    List<Location> findAllByDivisionParentIdIn(List<Long> listId);
}
