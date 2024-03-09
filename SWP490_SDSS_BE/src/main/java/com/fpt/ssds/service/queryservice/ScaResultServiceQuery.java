package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.ScaResultRepository;
import com.fpt.ssds.service.criteria.ScaResultCriteria;
import com.fpt.ssds.service.dto.ScaResultDto;
import com.fpt.ssds.service.mapper.ScaResultMapper;
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
public class ScaResultServiceQuery extends QueryService<ScaResult> {
    private final ScaResultRepository scaResultRepository;

    private final ScaResultMapper scaResultMapper;

    public Page<ScaResultDto> findByCriteria(ScaResultCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ScaResult> specification = createSpecification(criteria);
        return scaResultRepository.findAll(specification, page)
            .map(scaResultMapper::toDto);
    }

    private Specification<ScaResult> createSpecification(ScaResultCriteria criteria) {
        Specification<ScaResult> scaResultSpecification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                scaResultSpecification = scaResultSpecification.and(buildRangeSpecification(criteria.getId(), ScaResult_.id));
            }
            if (criteria.getCreatedDate() != null) {
                scaResultSpecification = scaResultSpecification.and(buildRangeSpecification(criteria.getCreatedDate(), ScaResult_.createdDate));
            }
            if (criteria.getCustomerId() != null) {
                scaResultSpecification = scaResultSpecification.and(buildSpecification(criteria.getCustomerId(),
                    service -> service.join(ScaResult_.customer, JoinType.LEFT).get(User_.id)));
            }
            if (criteria.getCustomerName() != null) {
                scaResultSpecification = scaResultSpecification.and(buildSpecification(criteria.getCustomerName(),
                    service -> service.join(ScaResult_.customer, JoinType.LEFT).get(User_.fullName)));
            }
            if (criteria.getCustomerPhoneNumber() != null) {
                scaResultSpecification = scaResultSpecification.and(buildSpecification(criteria.getCustomerPhoneNumber(),
                    service -> service.join(ScaResult_.customer, JoinType.LEFT).get(User_.phoneNumber)));
            }
            if (criteria.getStatusCode() != null) {
                scaResultSpecification = scaResultSpecification.and(buildSpecification(criteria.getStatusCode(),
                    service -> service.join(ScaResult_.status, JoinType.LEFT).get(Lookup_.code)));
            }
            if (criteria.getRepliedByName() != null) {
                scaResultSpecification = scaResultSpecification.and(buildSpecification(criteria.getRepliedByName(),
                    service -> service.join(ScaResult_.repliedBy, JoinType.LEFT).get(User_.fullName)));
            }
        }

        return scaResultSpecification;
    }
}
