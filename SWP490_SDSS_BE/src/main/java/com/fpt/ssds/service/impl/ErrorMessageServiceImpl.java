package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.ErrorMessage;
import com.fpt.ssds.repository.ErrorMessageRepository;
import com.fpt.ssds.service.ErrorMessageService;
import com.fpt.ssds.service.dto.ErrorMessageDto;
import com.fpt.ssds.service.mapper.ErrorMessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ErrorMessageServiceImpl implements ErrorMessageService {
    private final Logger log = LoggerFactory.getLogger(ErrorMessageServiceImpl.class);
    private final ErrorMessageRepository errorMessageRepository;

    private final ErrorMessageMapper errorMessageMapper;

    @Autowired
    public ErrorMessageServiceImpl(ErrorMessageRepository errorMessageRepository, ErrorMessageMapper errorMessageMapper) {
        this.errorMessageRepository = errorMessageRepository;
        this.errorMessageMapper = errorMessageMapper;
    }

    @Override
    public ErrorMessageDto findByCode(String code) {
        Optional<ErrorMessage> errorMessage = errorMessageRepository.findByCode(code);
        return errorMessage.isPresent() ? errorMessageMapper.toDto(errorMessage.get()) : null;
    }
}
