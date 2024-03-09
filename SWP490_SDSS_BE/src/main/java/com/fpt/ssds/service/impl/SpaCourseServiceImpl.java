package com.fpt.ssds.service.impl;

import com.fpt.ssds.service.SpaCourseService;
import com.fpt.ssds.service.dto.SpaCourseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpaCourseServiceImpl implements SpaCourseService {

    @Override
    public void createUpdate(SpaCourseDTO spaCourseDTO) {
        if (Objects.nonNull(spaCourseDTO.getId())) {

        } else {
            createCourse(spaCourseDTO);
        }
    }

    private void createCourse(SpaCourseDTO spaCourseDTO) {

    }
}
