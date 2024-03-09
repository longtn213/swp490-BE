package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.ErrorMessageDto;

public interface ErrorMessageService {
    ErrorMessageDto findByCode(String code);
}
