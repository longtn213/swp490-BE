package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.service.dto.BranchDto;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Branch} and its DTO {@link BranchDto}.
 */
@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface BranchMapper extends EntityMapper<BranchDto, Branch> {

    default Branch fromId(Long id) {
        if (id == null) {
            return null;
        }
        Branch branch = new Branch();
        branch.setId(id);
        return branch;
    }
}
