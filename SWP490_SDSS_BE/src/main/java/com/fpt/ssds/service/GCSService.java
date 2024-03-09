package com.fpt.ssds.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;

public interface GCSService extends StorageService {
    Blob uploadObject(String projectId, String bucketName, String objectName, byte[] fileData);

    Storage getStorage(String projectId);
}
