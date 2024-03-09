package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.FileRepository;
import com.fpt.ssds.repository.SpaServiceRepository;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.dto.SpaServiceDto;
import com.fpt.ssds.service.criteria.ServiceCriteria;
import com.fpt.ssds.service.mapper.FileMapper;
import com.fpt.ssds.service.mapper.SpaServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaServiceQueryService extends QueryService<SpaService> {
    private final SpaServiceRepository spaServiceRepository;

    private final SpaServiceMapper spaServiceMapper;

    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    @Transactional(readOnly = true)
    public Page<SpaServiceDto> findByCriteria(ServiceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SpaService> specification = createSpecification(criteria);
        Page<SpaService> result = spaServiceRepository.findAll(specification, page);
        Page<SpaServiceDto> serviceDtos = result.map(spaServiceMapper::toDto);
        enrichInfo(serviceDtos);
        return serviceDtos;
    }

    private void enrichInfo(Page<SpaServiceDto> serviceDtos) {
        List<Long> servicesId = serviceDtos.stream().map(SpaServiceDto::getId).collect(Collectors.toList());
        List<File> files = fileRepository.findByTypeAndRefIdInAndUploadStatus(FileType.SERVICE, servicesId, UploadStatus.SUCCESS);
        Map<Long, List<FileDto>> filesByRefId = fileMapper.toDto(files).stream().collect(Collectors.groupingBy(FileDto::getRefId));
        for (SpaServiceDto serviceDto : serviceDtos) {
            serviceDto.setFiles(filesByRefId.get(serviceDto.getId()));
        }
    }

    protected Specification<SpaService> createSpecification(ServiceCriteria criteria) {
        Specification<SpaService> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SpaService_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), SpaService_.name));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), SpaService_.code));
            }
            if (criteria.getDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDuration(), SpaService_.duration));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), SpaService_.isActive));
            }
            if (criteria.getCurrentPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCurrentPrice(), SpaService_.currentPrice));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), SpaService_.description));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), SpaService_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), SpaService_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), SpaService_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), SpaService_.lastModifiedDate));
            }
            if (criteria.getCategoryCode() != null) {
                specification = specification.and(buildSpecification(criteria.getCategoryCode(),
                    service -> service.join(SpaService_.category, JoinType.LEFT).get(Category_.code)));
            }
            if (criteria.getEquipmentCode() != null) {
                specification = specification.and(buildSpecification(criteria.getEquipmentCode(),
                    service -> service.join(SpaService_.equipmentType, JoinType.LEFT).get(EquipmentType_.code)));
            }
            if (criteria.getBookedCount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBookedCount(), SpaService_.bookedCount));
            }
        }
        return specification;
    }
}
