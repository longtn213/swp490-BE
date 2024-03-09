package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Lookup;
import com.fpt.ssds.service.dto.LookupDto;
import org.mapstruct.Mapper;


/**
 * Mapper for the entity {@link Lookup} and its DTO {@link LookupDto}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface LookupMapper extends EntityMapper<Lookup, LookupDto> {
    default Lookup fromId(Long id) {
        if (id == null) {
            return null;
        }
        Lookup lookup = new Lookup();
        lookup.setId(id);
        return lookup;
    }
}
