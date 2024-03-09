package com.fpt.ssds.service.impl;

import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.domain.File;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.StorageSource;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.FileRepository;
import com.fpt.ssds.service.ImgurService;
import com.fpt.ssds.service.SSDSStorageAdapter;
import com.fpt.ssds.service.SSDSStorageService;
import com.fpt.ssds.service.StorageService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.dto.SSDSStorageDTO;
import com.fpt.ssds.service.mapper.FileMapper;
import com.fpt.ssds.utils.ImageUtils;
import com.google.cloud.storage.Blob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSDSStorageServiceImpl implements SSDSStorageService {
    @Value("${wes.upload-attachment.max-file-size:2.0}")
    private float defaultMaxFileSizeInMB;

    @Value("${ssds.gcs.prefix}")
    private String prefix;

    private final SSDSStorageAdapter ssdsStorageAdapter;

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    private static final String SSDS_GCS_URL = "%s/%s/%s";

    private final ImgurService imgurService;


    @Override
    public void verifyUploadAttachment(MultipartFile multipartFile, String type) {
        if (StringUtils.isEmpty(type)) {
            throw new SSDSBusinessException(ErrorConstants.FILE_TYPE_IS_REQUIRED);
        }
        if (!FileType.isExisted(type)) {
            throw new SSDSBusinessException(ErrorConstants.FILE_TYPE_IS_INVALID);
        }
        if (multipartFile.isEmpty()) {
            throw new SSDSBusinessException(ErrorConstants.FILE_IS_EMPTY);
        }
        double sizeInMB = ImageUtils.getImageFileSizeInMB(multipartFile);
        if (sizeInMB > defaultMaxFileSizeInMB) {
            throw new SSDSBusinessException(ErrorConstants.FILE_SIZE_EXCEEDS_LIMIT);
        }
    }

    @Override
    public FileDto uploadFile(MultipartFile multipartFile, String type) {
        File file = new File();
        file.setType(FileType.getFileType(type));
        file.setStorageSource(StorageSource.GCS);
        try {
            StorageService storageService = ssdsStorageAdapter.getSSDSStorageService(StorageSource.GCS.getValue());
//            SSDSStorageDTO ssdsStorageDTO = storageService.uploadImageFile(multipartFile.getOriginalFilename(), multipartFile.getBytes(), FileType.getFileType(type).toString());
            SSDSStorageDTO ssdsStorageDTO = storageService.uploadImageFileToPublicBucket(multipartFile.getOriginalFilename(), multipartFile.getBytes(), FileType.getFileType(type).toString());

            if (UploadStatus.SUCCESS.equals(ssdsStorageDTO.getUploadStatus())) {
                String url = String.format(SSDS_GCS_URL, prefix, ssdsStorageDTO.getBucketName(), ssdsStorageDTO.getObjectName());
                file.setUploadStatus(UploadStatus.SUCCESS);
                file.setMetadata(ssdsStorageDTO.toString());
                file.setUrl(url);
            } else {
                throw new SSDSBusinessException(ErrorConstants.FILE_UPLOAD_ERROR);
            }
        } catch (Exception ex) {
            log.info("ERROR uploadFile:  {}", ex.getMessage(), ex);
            throw new SSDSBusinessException(ErrorConstants.FILE_UPLOAD_ERROR);
        }

        if (file.getStartUploadTime() == null) {
            file.setStartUploadTime(Instant.now());
        }
        file.setFinishUploadTime(Instant.now());
        double sizeInMB = ImageUtils.getImageFileSizeInMB(multipartFile);
        file.setSize(sizeInMB);
        File result = fileRepository.save(file);
        return (fileMapper.toDto(result));
    }

    @Override
    public FileDto uploadFileImport(MultipartFile multipartFile, String type, String failedReason) {
        File file = new File();
        try {
            file.setFailReason(failedReason);
            file.setType(FileType.getFileType(type));
            file.setStorageSource(StorageSource.GCS);
            StorageService storageService = ssdsStorageAdapter.getSSDSStorageService(StorageSource.GCS.getValue());
            SSDSStorageDTO ssdsStorageDTO = storageService.uploadFile(multipartFile.getOriginalFilename(), multipartFile.getBytes(), FileType.getFileType(type).toString());
            if (UploadStatus.SUCCESS.equals(ssdsStorageDTO.getUploadStatus())) {
                file.setUploadStatus(UploadStatus.SUCCESS);
                file.setMetadata(ssdsStorageDTO.toString());
            }
            String url = String.format(SSDS_GCS_URL, prefix, ssdsStorageDTO.getBucketName(), ssdsStorageDTO.getObjectName());
            file.setUrl(url);
            if (file.getStartUploadTime() == null) {
                file.setStartUploadTime(Instant.now());
            }
            file.setFinishUploadTime(Instant.now());
            double sizeInMB = ImageUtils.getImageFileSizeInMB(multipartFile);
            file.setSize(sizeInMB);
            File result = fileRepository.save(file);
            return (fileMapper.toDto(result));
        } catch (Exception ex) {
            log.error("error uploadFileImport {}", ex.getMessage(), ex);
            file.setUrl(multipartFile.getOriginalFilename());
            file.setUploadStatus(UploadStatus.FAILED);
            File result = fileRepository.save(file);
            return (fileMapper.toDto(result));
        }
    }

    @Override
    public URL download(File file) {
        StorageService storageService = ssdsStorageAdapter.getSSDSStorageService(file.getStorageSource().getValue());
        URL url = storageService.download(file.getMetadata());
        return url;
    }

    @Override
    public Blob download(String projectId, String bucketName, String objectName) {
        StorageService storageService = ssdsStorageAdapter.getSSDSStorageService(StorageSource.GCS.getValue());
//        Blob blob = storageService.downloadObject(projectId, bucketName, objectName);
//        if (Objects.isNull(blob)) {
//            return null;
//        }
//        ReadChannel reader = blob.reader();
//        InputStream inputStream = Channels.newInputStream(reader);
        return storageService.downloadObject(projectId, bucketName, objectName);
    }

    @Override
    public boolean deleteObject(String projectId, String bucketName, String objectName) {
        StorageService storageService = ssdsStorageAdapter.getSSDSStorageService(StorageSource.GCS.getValue());
        return storageService.deleteObject(projectId, bucketName, objectName);
    }

    @Override
    public FileDto uploadFileToImgur(MultipartFile multipartFile, String type) {
        File file = new File();
        file.setType(FileType.getFileType(type));
        file.setStorageSource(StorageSource.IMGUR);
        try {
            String url = imgurService.uploadFile(multipartFile);
            file.setUploadStatus(UploadStatus.SUCCESS);
            file.setUrl(url);
        } catch (Exception ex) {
            log.info("ERROR uploadFile:  {}", ex.getMessage(), ex);
            throw new SSDSBusinessException(ErrorConstants.FILE_UPLOAD_ERROR);
        }

        if (file.getStartUploadTime() == null) {
            file.setStartUploadTime(Instant.now());
        }
        file.setFinishUploadTime(Instant.now());
        double sizeInMB = ImageUtils.getImageFileSizeInMB(multipartFile);
        file.setSize(sizeInMB);
        File result = fileRepository.save(file);
        return (fileMapper.toDto(result));
    }
}
