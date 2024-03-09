package com.fpt.ssds.repository;

import com.fpt.ssds.domain.ReasonMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonMessageRepository extends JpaRepository<ReasonMessage, Long>, JpaSpecificationExecutor<ReasonMessage> {

    @Query("select c from ReasonMessage c " +
        "join c.reasonMessageType type " +
        "where c.isActive = true " +
        "and c.id in :listReasonId " +
        "and type.code = :type")
    List<ReasonMessage> findAllActiveReasonMessageByIdAndReasonMessageTypeCode(@Param("listReasonId") List<Long> listReasonId, @Param("type") String type);

    @Query("select rm from ReasonMessage rm where rm.reasonMessageType.code = :reasonCode and rm.isActive = true")
    List<ReasonMessage> findActiveReasonMessageByType(@Param("reasonCode") String reasonCode);

}
