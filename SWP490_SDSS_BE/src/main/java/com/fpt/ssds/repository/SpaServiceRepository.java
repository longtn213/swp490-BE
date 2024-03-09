package com.fpt.ssds.repository;

import com.fpt.ssds.domain.SpaService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaServiceRepository extends JpaRepository<SpaService, Long>, JpaSpecificationExecutor<SpaService> {
    Optional<SpaService> findByCode(String code);

    List<SpaService> findByCategoryIdIn(List<Long> listCategoryId);

    List<SpaService> findByIdIn(List<Long> listServiceId);

    List<SpaService> findByEquipmentTypeIdIn(List<Long> listId);

    List<SpaService> findByCategoryIdIsNull();
}
