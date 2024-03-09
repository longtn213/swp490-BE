package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Price;
import com.fpt.ssds.service.dto.PriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SpaServiceMapper.class})
public interface PriceMapper extends EntityMapper<PriceDto, Price> {
    @Mapping(source = "spaService.id", target = "spaServiceId")
    PriceDto toDto(Price price);

    @Mapping(source = "spaServiceId", target = "spaService")
    Price toEntity(PriceDto priceDto);

    default Price fromId(Long id) {
        if (id == null) {
            return null;
        }
        Price price = new Price();
        price.setId(id);
        return price;
    }
}
