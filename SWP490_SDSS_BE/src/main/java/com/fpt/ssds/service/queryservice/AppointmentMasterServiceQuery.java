package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.AppointmentMasterRepository;
import com.fpt.ssds.service.dto.AppointmentMasterDto;
import com.fpt.ssds.service.criteria.AppointmentMasterCriteria;
import com.fpt.ssds.service.mapper.AppointmentMasterMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentMasterServiceQuery extends QueryService<AppointmentMaster> {
    private final AppointmentMasterRepository appointmentMasterRepository;
    private final AppointmentMasterMapper appointmentMasterMapper;

    public Page<AppointmentMasterDto> findByCriteria(AppointmentMasterCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AppointmentMaster> specification = createSpecification(criteria);
        return appointmentMasterRepository.findAll(specification, page)
            .map(appointmentMasterMapper::toDto);
    }

    protected Specification<AppointmentMaster> createSpecification(AppointmentMasterCriteria criteria) {
        Specification<AppointmentMaster> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AppointmentMaster_.id));
            }
            if (criteria.getExpectedStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpectedStartTime(), AppointmentMaster_.expectedStartTime));
            }
            if (criteria.getExpectedEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getExpectedEndTime(), AppointmentMaster_.expectedEndTime));
            }
            if (criteria.getActualStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getActualStartTime(), AppointmentMaster_.actualStartTime));
            }
            if (criteria.getActualEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getActualEndTime(), AppointmentMaster_.actualEndTime));
            }
            if (criteria.getCancelTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCancelTime(), AppointmentMaster_.cancelTime));
            }
            if (criteria.getCancelBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCancelBy(), AppointmentMaster_.cancelBy));
            }
            if (criteria.getTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotal(), AppointmentMaster_.total));
            }
            if (criteria.getPayAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPayAmount(), AppointmentMaster_.payAmount));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), AppointmentMaster_.note));
            }
            if (criteria.getOverdueStatus() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOverdueStatus(), AppointmentMaster_.overdueStatus));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), AppointmentMaster_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), AppointmentMaster_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), AppointmentMaster_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), AppointmentMaster_.lastModifiedDate));
            }
            if (criteria.getPaymentMethod() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPaymentMethod(), AppointmentMaster_.paymentMethod));
            }
            if (criteria.getBranchCode() != null) {
                specification = specification.and(buildSpecification(criteria.getBranchCode(),
                    service -> service.join(AppointmentMaster_.branch, JoinType.LEFT).get(Branch_.code)));
            }
            if (criteria.getCustomerName() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerName(),
                    service -> service.join(AppointmentMaster_.customer, JoinType.LEFT).get(User_.fullName)));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerId(),
                    service -> service.join(AppointmentMaster_.customer, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getCustomerPhoneNumber() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerPhoneNumber(),
                    service -> service.join(AppointmentMaster_.customer, JoinType.LEFT).get(User_.phoneNumber)));
            }
            if (criteria.getStatusCode() != null) {
                specification = specification.and(buildSpecification(criteria.getStatusCode(),
                    service -> service.join(AppointmentMaster_.status, JoinType.LEFT).get(Lookup_.code)));
            }
            if (criteria.getCancelReason() != null) {
                specification = specification.and(buildSpecification(criteria.getCancelReason(),
                    service -> service.join(AppointmentMaster_.canceledReason, JoinType.LEFT).get(ReasonMessage_.code)));
            }
        }
        return specification;
    }
}
