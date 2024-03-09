package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.dto.UserListingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface UserListingMapper extends EntityMapper<UserListingDTO, User> {
    @Override
    @Mapping(target = "notifications", ignore = true)
    User toEntity(UserListingDTO dto);

    default User fromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
