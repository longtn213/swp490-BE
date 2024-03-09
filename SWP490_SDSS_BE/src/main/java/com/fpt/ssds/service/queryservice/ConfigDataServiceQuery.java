package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.ConfigDataRepository;
import com.fpt.ssds.service.dto.ConfigDataDTO;
import com.fpt.ssds.service.criteria.ConfigDataCriteria;
import com.fpt.ssds.service.mapper.ConfigDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;

@Service
@Slf4j
public class ConfigDataServiceQuery extends QueryService<ConfigData> {

    private final ConfigDataRepository configDataRepository;

    private final ConfigDataMapper configDataMapper;

    @Autowired
    public ConfigDataServiceQuery(ConfigDataRepository configDataRepository, ConfigDataMapper configDataMapper) {
        this.configDataRepository = configDataRepository;
        this.configDataMapper = configDataMapper;
    }

    public Page<ConfigDataDTO> findByCriteria(ConfigDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ConfigData> specification = createSpecification(criteria);
        return configDataRepository.findAll(specification, page)
            .map(configDataMapper::toDto);
    }

    protected Specification<ConfigData> createSpecification(ConfigDataCriteria criteria) {
        Specification<ConfigData> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ConfigData_.id));
            }
            if (criteria.getConfigKey() != null) {
                specification = specification.and(buildStringSpecification(criteria.getConfigKey(), ConfigData_.configKey));
            }
            if (criteria.getConfigValue() != null) {
                specification = specification.and(buildStringSpecification(criteria.getConfigValue(), ConfigData_.configValue));
            }
            if (criteria.getAllowUpdate() != null) {
                specification = specification.and(buildSpecification(criteria.getAllowUpdate(), ConfigData_.allowUpdate));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), ConfigData_.type));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), ConfigData_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), ConfigData_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), ConfigData_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), ConfigData_.lastModifiedDate));
            }
            if (criteria.getBranchCode() != null) {
                specification = specification.and(buildSpecification(criteria.getBranchCode(),
                    service -> service.join(ConfigData_.branch, JoinType.LEFT).get(Branch_.code)));
            }
            if (criteria.getBranchId() != null) {
                specification = specification.and(buildSpecification(criteria.getBranchId(),
                    service -> service.join(ConfigData_.branch, JoinType.LEFT).get(Branch_.id)));
            }
        }
        return specification;
    }

}
