package com.fpt.ssds.service.queryservice;

import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.domain.*;
import com.fpt.ssds.repository.UserRepository;
import com.fpt.ssds.service.criteria.UserCriteria;
import com.fpt.ssds.service.dto.UserDto;
import com.fpt.ssds.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.jhipster.service.QueryService;

import javax.persistence.criteria.JoinType;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserQueryService extends QueryService<User> {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public Page<UserDto> findByCriteria(UserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<User> specification = createSpecification(criteria);
        Page<UserDto> userDtos = userRepository.findAll(specification, page)
            .map(userMapper::toDto);
        for (UserDto userDto : userDtos) {
            if (Objects.nonNull(userDto.getEmail()) && userDto.getEmail().startsWith(Constants.FAKE_EMAIL_PREFIX)) {
                userDto.setEmail(null);
            }
        }
        return userDtos;
    }

    private Specification<User> createSpecification(UserCriteria criteria) {
        Specification<User> userSpecification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                userSpecification = userSpecification.and(buildRangeSpecification(criteria.getId(), User_.id));
            }
            if (criteria.getFullName() != null) {
                userSpecification = userSpecification.and(buildStringSpecification(criteria.getFullName(), User_.fullName));
            }
            if (criteria.getPhoneNumber() != null) {
                userSpecification = userSpecification.and(buildStringSpecification(criteria.getPhoneNumber(), User_.phoneNumber));
            }
            if (criteria.getGender() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getGender(), User_.gender));
            }
            if (criteria.getEmail() != null) {
                userSpecification = userSpecification.and(buildStringSpecification(criteria.getEmail(), User_.email));
            }
            if (criteria.getUsername() != null) {
                userSpecification = userSpecification.and(buildStringSpecification(criteria.getUsername(), User_.username));
            }
            if (criteria.getIsActive() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getIsActive(), User_.isActive));
            }
            if (criteria.getRoleName() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getRoleName(),
                    service -> service.join(User_.role, JoinType.LEFT).get(Role_.name)));
            }
            if (criteria.getBranchName() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getBranchName(),
                    service -> service.join(User_.branch, JoinType.LEFT).get(Branch_.name)));
            }
            if (criteria.getRoleCode() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getRoleCode(),
                    service -> service.join(User_.role, JoinType.LEFT).get(Role_.code)));
            }
            if (criteria.getBranchCode() != null) {
                userSpecification = userSpecification.and(buildSpecification(criteria.getBranchCode(),
                    service -> service.join(User_.branch, JoinType.LEFT).get(Branch_.code)));
            }
        }

        return userSpecification;
    }
}
