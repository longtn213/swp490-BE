package com.fpt.ssds.repository;

import com.fpt.ssds.domain.EquipmentType;
import com.fpt.ssds.domain.SpaService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
    Optional<EquipmentType> findByCode(String code);
}
