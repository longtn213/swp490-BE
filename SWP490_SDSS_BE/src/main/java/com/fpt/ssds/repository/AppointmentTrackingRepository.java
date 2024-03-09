package com.fpt.ssds.repository;

import com.fpt.ssds.domain.AppointmentTracking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentTrackingRepository extends JpaRepository<AppointmentTracking, Long>, JpaSpecificationExecutor<AppointmentTracking> {
    List<AppointmentTracking> findByTimeGreaterThan(Instant time);

    @Query("select c from AppointmentTracking c")
    List<AppointmentTracking> findLastestTrackingTime(Pageable pageable);

    List<AppointmentTracking> findByBranchIdAndTimeGreaterThanAndTimeLessThan(Long branchId, Instant now, Instant endtime);

    List<AppointmentTracking> findAllByTimeBetweenAndBranchId(Instant startTime, Instant endTime, Long branchId);

    List<AppointmentTracking> findByBranchIdAndTimeGreaterThanAndAndBookedQtyGreaterThan(Long branchId, Instant startTime, Long bookedQty);

    List<AppointmentTracking> findByBranchIdAndTimeGreaterThanOrderByTime(Long branchId, Instant time);
}
