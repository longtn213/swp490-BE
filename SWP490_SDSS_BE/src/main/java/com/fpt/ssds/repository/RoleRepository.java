package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String customer);

    List<Role> findByCodeIn(Set<String> roleSet);
}
