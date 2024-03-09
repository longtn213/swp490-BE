package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.AppointmentTrackingRepository;
import com.fpt.ssds.service.criteria.AppointmentTrackingCriteria;
import com.fpt.ssds.service.dto.AppointmentTrackingDto;
import com.fpt.ssds.service.mapper.AppointmentTrackingMapper;
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
public class AppointmentTrackingServiceQuery extends QueryService<AppointmentTracking> {
    private final AppointmentTrackingRepository appointmentTrackingRepository;
    private final AppointmentTrackingMapper appointmentTrackingMapper;

    public Page<AppointmentTrackingDto> findByCriteria(AppointmentTrackingCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AppointmentTracking> specification = createSpecification(criteria);
        return appointmentTrackingRepository.findAll(specification, page)
            .map(appointmentTrackingMapper::toDto);
    }

    private Specification<AppointmentTracking> createSpecification(AppointmentTrackingCriteria criteria) {
        Specification<AppointmentTracking> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AppointmentTracking_.id));
            }
            if (criteria.getTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTime(), AppointmentTracking_.time));
            }
            if (criteria.getMaxQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMaxQty(), AppointmentTracking_.maxQty));
            }
            if (criteria.getBookedQty() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBookedQty(), AppointmentTracking_.bookedQty));
            }
            if (criteria.getIsAvailable() != null) {
                specification = specification.and(buildSpecification(criteria.getIsAvailable(), AppointmentTracking_.isAvailable));
            }
            if (criteria.getBranchCode() != null) {
                specification = specification.and(buildSpecification(criteria.getBranchCode(),
                    service -> service.join(AppointmentTracking_.branch, JoinType.LEFT).get(Branch_.code)));
            }
        }
        return specification;
    }

}
