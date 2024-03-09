package com.fpt.ssds.service;

import com.fpt.ssds.domain.File;
import com.fpt.ssds.service.dto.FileDto;
import com.google.cloud.storage.Blob;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface SSDSStorageService {
    void verifyUploadAttachment(MultipartFile multipartFile, String type);

    FileDto uploadFile(MultipartFile multipartFile, String type);

    FileDto uploadFileImport(MultipartFile multipartFile, String type, String failedReason);

    URL download(File file);

    Blob download(String projectId, String bucketName, String objectName);

    boolean deleteObject(String projectId, String bucketName, String objectName);

    FileDto uploadFileToImgur(MultipartFile file, String type);
}
