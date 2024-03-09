package com.fpt.ssds.service;

import com.fpt.ssds.domain.Lookup;

public interface LookupService {
    Lookup findByKeyAndCode(String lookupKey, String code);
}
