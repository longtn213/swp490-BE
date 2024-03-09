package com.fpt.ssds.repository;

import com.fpt.ssds.domain.AppointmentService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentServiceRepository extends JpaRepository<AppointmentService, Long> {
    @Query("select c from AppointmentService c " +
        "join c.appointmentMaster am " +
        "where c.actualEndTime>=:startTime " +
        "and c.actualEndTime<= :endTime " +
        "and c.status.code = :statusCode " +
        "and am.branch.id = :branchId")
    List<AppointmentService> findByStatusAndActualEndTimeAndBranch(@Param("statusCode") String statusCode, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime, @Param("branchId") Long branchId);
}
