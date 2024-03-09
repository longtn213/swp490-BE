package com.fpt.ssds.repository;

import com.fpt.ssds.domain.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {
    @Query("select c from Price c " +
        "join c.spaService s " +
        "where s.id = :serviceId and c.endDate is null")
    Optional<Price> findLastestPriceByServiceId(@Param("serviceId") Long serviceId);

    @Query("select SUM(c.price) from Price c " +
        "join c.spaService s " +
        "where s.id in :existedServicesId and c.endDate is null")
    Double getTotalByListService(@Param("existedServicesId") List<Long> existedServicesId);
}
