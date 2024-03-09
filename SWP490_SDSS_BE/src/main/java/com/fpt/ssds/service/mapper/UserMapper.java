package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link User} and its DTO {@link UserDto}.
 */
@Mapper(componentModel = "spring", uses = {RoleMapper.class, BranchMapper.class})
public interface UserMapper extends EntityMapper<UserDto, User> {
    @Override
    @Mapping(target = "notifications", ignore = true)
    User toEntity(UserDto dto);

    default User fromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
