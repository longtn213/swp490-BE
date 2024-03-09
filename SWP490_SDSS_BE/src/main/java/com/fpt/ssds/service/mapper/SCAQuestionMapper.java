package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.ScaQuestion;
import com.fpt.ssds.service.dto.SCAQuestionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ScaFormMapper.class})
public interface SCAQuestionMapper extends EntityMapper<SCAQuestionDto, ScaQuestion> {
    @Override
    @Mapping(source = "form.id", target = "formId")
    SCAQuestionDto toDto(ScaQuestion entity);

    @Mapping(target = "options", ignore = true)
    @Mapping(source = "formId", target = "form")
    ScaQuestion toEntity(SCAQuestionDto scaQuestionDto);

    default ScaQuestion fromId(Long id) {
        if (id == null) {
            return null;
        }
        ScaQuestion scaQuestion = new ScaQuestion();
        scaQuestion.setId(id);
        return scaQuestion;
    }
}
