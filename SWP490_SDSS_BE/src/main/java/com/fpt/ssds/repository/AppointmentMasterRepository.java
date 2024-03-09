package com.fpt.ssds.repository;

import com.fpt.ssds.domain.AppointmentMaster;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.dto.MetricPerHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentMasterRepository extends JpaRepository<AppointmentMaster, Long>, JpaSpecificationExecutor<AppointmentMaster> {
    List<AppointmentMaster> findByExpectedStartTimeAndCustomerIdAndStatusCodeNotIn(Instant expectedStartTime, Long id, List<String> asList);

    @Query("select count (distinct service.id) " +
        "from AppointmentMaster am " +
        "join am.appointmentServices service " +
        "join service.status status " +
        "where am.id = :apptMasterId " +
        "and status.lookupKey = :lookupKey " +
        "and status.code not in :serviceStatus")
    int countByServiceStatusNotIn(@Param("apptMasterId") Long apptMasterId, @Param("lookupKey") String lookupKey, @Param("serviceStatus") List<String> serviceStatus);

    @Query("select distinct c from AppointmentMaster c " +
        "join c.appointmentServices service " +
        "where service.id in :lstApptServiceId")
    List<AppointmentMaster> findAllByServiceId(@Param("lstApptServiceId") List<Long> lstApptServiceId);

    List<AppointmentMaster> findByExpectedStartTimeInAndStatusCodeNotInAndAndBranchId(List<Instant> lstExpectedStartTime, List<String> statusCode, Long branchId);

    @Query("select count (distinct am) from AppointmentMaster am " +
        "where am.createdDate >= :startTime and am.createdDate <= :endTime " +
        "and am.overdueStatus = :overdueStatus " +
        "and am.branch.id = :branchId")
    Integer countOverdueApptBetweenTimeAndBranchId(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime, @Param("overdueStatus") String overdueStatus, @Param("branchId") Long branchId);

    Integer countByActualEndTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    Integer countByCancelTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    Integer countAmByCreatedDateBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    Integer countAmByActualStartTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    List<AppointmentMaster> findByExpectedStartTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    List<AppointmentMaster> findByActualStartTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    List<AppointmentMaster> findByActualEndTimeBetweenAndStatusCodeAndBranchId(Instant startTime, Instant endTime, String statusCode, Long branchId);

    Optional<AppointmentMaster> findByBranchCodeAndId(String branchCode, Long amId);

    @Query("select new com.fpt.ssds.service.dto.AppointmentMasterDto(am.id, am.expectedStartTime, am.expectedEndTime, am.actualStartTime, am.actualEndTime," +
        "am.cancelTime, am.total, am.payAmount, b.id, b.code, b.name, status.code) from AppointmentMaster am " +
        "join am.branch b " +
        "join am.status status")
    List<AppointmentMasterDto> getListApptMaster(Instant startTime, Instant endTime);

    List<AppointmentMaster> findByCreatedDateBetweenAndBranchId(Instant startTime, Instant endTime, Long branchId);

    List<AppointmentMaster> findByActualEndTimeBetweenAndBranchIdAndStatusCode(Instant startTime, Instant endTime, Long id, String statusCode);

    List<AppointmentMaster> findByCancelTimeBetweenAndBranchId(Instant startOfDay, Instant endOfDay, Long branchId);

    @Query(nativeQuery = true, value =
        "select hour(am.created_date) as time, count(am.id) as numOfAm, sum(am.pay_amount) as total, 'GMV' as type " +
            "from appointment_master am " +
            "join lookup l on am.status_id = l.id " +
            "join branch b on am.branch_id = b.id " +
            "where am.created_date>= ?2 and am.created_date<= ?3 and b.id = ?1 " +
            "group by time " +
            "union " +
            "select hour(am.actual_end_time) as time, count(am.id) as numOfAm, sum(am.pay_amount) as total, 'DONE' as type " +
            "from appointment_master am " +
            "join lookup l on am.status_id = l.id " +
            "join branch b on am.branch_id = b.id " +
            "where am.actual_end_time>= ?2 and am.actual_end_time<= ?3 and l.lookup_value = 'CLOSED' and b.id = ?1 " +
            "group by time " +
            "union " +
            "select hour(am.cancel_time) as time, count(am.id) as numOfAm, sum(am.pay_amount) as total, 'CANCELED' as type " +
            "from appointment_master am " +
            "join lookup l on am.status_id = l.id " +
            "join branch b on am.branch_id = b.id " +
            "where am.cancel_time>= ?2 and am.cancel_time<= ?3 and b.id = ?1 " +
            "group by time")
    List<MetricPerHour> calculateMetricInDay(@Param("branchId") Long branchId, @Param("from") Instant from, @Param("to") Instant to);

    Integer countByBranchIdAndStatusCodeIn(Long branchId, List<String> statusList);

    @Query("select c from AppointmentMaster c where c.expectedStartTime <= :time and c.overdueStatus is null")
    List<AppointmentMaster> findAmNeedEnrichOverdueInfo(@Param("time") Instant time);

    @Query("select c from AppointmentMaster c where c.confirmMessageSent is false or c.confirmMessageSent is null and c.status.code = 'READY'")
    List<AppointmentMaster> findAmNeedSendMessageConfirm();
}
