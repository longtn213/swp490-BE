package com.fpt.ssds.repository;

import com.fpt.ssds.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

    Optional<User> findByPhoneNumberAndRefIdIsNull(String phoneNumber);

    @Query("select c from User c " +
        "join c.role r " +
        "where r.code = :role")
    List<User> findByRole(@Param("role") String role);

    @Query("select c from User c " +
        "join c.role r " +
        "where r.code = :role and c.id = :id ")
    Optional<User> findByRoleAndId(@Param("role") String role, @Param("id") Long id);

    @Query("select c from User c " +
        "join c.role r " +
        "join c.branch b " +
        "where r.code = :roleCode and b.id = :branchId")
    List<User> findByRoleAndBranch(@Param("roleCode") String roleCode, @Param("branchId") Long branchId);

    List<User> findByRefId(String refId);

    @Query("select u from User u " +
        "where u.username = :username " +
        "or u.phoneNumber = :username " +
        "or u.email = :username")
    Optional<User> getUserByUsername(@Param("username") String username);

    @Query(value = "select distinct(u.id) " +
        "from appointment_service ase " +
        "join user u on ase.specialist_id = u.id " +
        "join appointment_master am on ase.appointment_master_id = am.id " +
        "join lookup l on ase.status_id = l.id " +
        "where am.branch_id = ?3 " +
        "and ((ase.expected_start_time>=?1 and ase.expected_start_time<=?2) " +
        "     or (ase.expected_end_time>=?1 and ase.expected_end_time<=?2) " +
        "     or (ase.expected_start_time<=?1 and ase.expected_end_time>=?2) " +
        "     and (l.lookup_value in ('WAITING_FOR_CONFIRMATION' , 'READY'))) " +
        "or ((ase.actual_start_time>=?1 and ase.actual_start_time<=?2) " +
        "     or (ase.actual_end_time>=?1 and ase.actual_end_time<=?2) " +
        "     or (ase.actual_start_time<=?1 and ase.actual_end_time>=?2) " +
        "     and (l.lookup_value in ('IN_PROGRESS')))", nativeQuery = true)
    List<Long> findSpecialistNotAvailableByTime(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime, @Param("branchId") Long branchId);

    List<User> findByIdIn(List<Long> specialistId);

    @Query(value = "select c.id from user c where (c.phone_number is not null and c.phone_number = ?1) " +
        "or (c.email is not null and c.email = ?2) " +
        "and c.id not in ?3", nativeQuery = true)
    List<Long> findByPhoneNumberOrEmailAndIdNotIn(@Param("phoneNumber") String phoneNumber, @Param("email") String email, @Param("id") List<Long> id);
}
