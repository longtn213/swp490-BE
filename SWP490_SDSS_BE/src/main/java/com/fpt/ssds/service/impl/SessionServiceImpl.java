package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.Session;
import com.fpt.ssds.repository.SessionRepository;
import com.fpt.ssds.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public Session findById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElse(null);
    }
}
