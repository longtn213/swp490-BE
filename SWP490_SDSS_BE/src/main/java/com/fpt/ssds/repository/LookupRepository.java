package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Lookup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, Long> {
    Optional<Lookup> findByLookupKeyAndCode(String lookupKey, String code);

    List<Lookup> findByLookupKey(String lookupKey);
}
