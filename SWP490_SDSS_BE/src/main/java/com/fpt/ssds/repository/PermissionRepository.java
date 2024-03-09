package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByCodeIn(Set<String> permissionCode);
}
