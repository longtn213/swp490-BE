package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.Option;
import com.fpt.ssds.service.dto.OptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SCAQuestionMapper.class})
public interface OptionMapper extends EntityMapper<OptionDto, Option> {
    @Override
    @Mapping(source = "question.id", target = "questionId")
    OptionDto toDto(Option entity);

    @Mapping(source = "questionId", target = "question")
    Option toEntity(OptionDto optionDto);

    default Option fromId(Long id) {
        if (id == null) {
            return null;
        }
        Option option = new Option();
        option.setId(id);
        return option;
    }
}
