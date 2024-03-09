package com.fpt.ssds.service;

import com.fpt.ssds.domain.Session;

public interface SessionService {
    Session findById(Long sessionId);
}
