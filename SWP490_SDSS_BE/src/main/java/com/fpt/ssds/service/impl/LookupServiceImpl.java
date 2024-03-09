package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.Lookup;
import com.fpt.ssds.repository.LookupRepository;
import com.fpt.ssds.service.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService {
    private final LookupRepository lookupRepository;

    @Override
    public Lookup findByKeyAndCode(String lookupKey, String code) {
        Optional<Lookup> lookupOpt = lookupRepository.findByLookupKeyAndCode(lookupKey, code);
        if (lookupOpt.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.LOOKUP_NOT_EXIST);
        }
        return lookupOpt.get();
    }
}
