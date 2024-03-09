package com.fpt.ssds.service.mapper;

import com.fpt.ssds.domain.QuestionAnswer;
import com.fpt.ssds.service.dto.QuestionAnswerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ScaResultMapper.class})
public interface QuestionAnswerMapper extends EntityMapper<QuestionAnswerDto, QuestionAnswer> {
    @Override
    @Mapping(source = "resultId", target = "result")
    QuestionAnswer toEntity(QuestionAnswerDto dto);

    @Override
    @Mapping(source = "result.id", target = "resultId")
    QuestionAnswerDto toDto(QuestionAnswer entity);

    default QuestionAnswer fromId(Long id) {
        if (id == null) {
            return null;
        }
        QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setId(id);
        return questionAnswer;
    }
}
