package com.fpt.ssds.service;

import com.fpt.ssds.domain.enumeration.StorageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SSDSStorageAdapter {
    @Autowired
    private GCSService gcsService;

    public StorageService getSSDSStorageService(String storageSource) {
        if (StorageSource.GCS.getValue().equals(storageSource)) {
            return gcsService;
        } else {
            return null;
        }
    }
}
