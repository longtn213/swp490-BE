package com.fpt.ssds.service;

import com.fpt.ssds.domain.File;
import com.fpt.ssds.service.dto.SSDSStorageDTO;
import com.google.cloud.storage.Blob;

import java.net.URL;

public interface StorageService {
    SSDSStorageDTO uploadImageFile(String filename, byte[] fileData, String attachmentType);

    URL download(String jsonMetadata);

    SSDSStorageDTO uploadFile(String filename, byte[] fileData, String attachmentType);

    Blob downloadObject(String projectId, String bucketName, String objectName);

    boolean deleteObject(String projectId, String bucketName, String objectName);

    SSDSStorageDTO uploadImageFileToPublicBucket(String filename, byte[] fileData, String attachmentType);
}
