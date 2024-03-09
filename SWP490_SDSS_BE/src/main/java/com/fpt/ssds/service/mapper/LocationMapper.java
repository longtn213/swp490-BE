package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Location;
import com.fpt.ssds.service.dto.LocationDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {
    default Location fromId(Long id) {
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }
}
