package com.fpt.ssds.repository;

import com.fpt.ssds.domain.PerformanceMetric;
import com.fpt.ssds.service.dto.PerformanceMetricDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {
    List<PerformanceMetric> findByTimeBetweenAndBranchId(Instant from, Instant to, Long branchId);

    @Query("select new com.fpt.ssds.service.dto.PerformanceMetricDTO(" +
        "SUM(pm.placeGmv), " +
        "SUM(pm.placeAppointment), " +
        "SUM(pm.doneGmv), " +
        "SUM(pm.doneAppointment), " +
        "SUM(pm.cancelledSales), " +
        "SUM(pm.cancelledAppointment)) " +
        "from PerformanceMetric pm where pm.time >= :startTime and pm.time<=:endTime " +
        "and pm.branch.id = :branchId")
    PerformanceMetricDTO sumMetricByTimeAndBranchId(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime, @Param("branchId") Long branchId);
}
