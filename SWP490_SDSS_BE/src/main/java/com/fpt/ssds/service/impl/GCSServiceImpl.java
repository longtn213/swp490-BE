package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.enumeration.StorageSource;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.service.GCSService;
import com.fpt.ssds.service.dto.SSDSStorageDTO;
import com.fpt.ssds.utils.DateUtils;
import com.fpt.ssds.utils.IOUtils;
import com.fpt.ssds.utils.ImageUtils;
import com.fpt.ssds.utils.JsonSupport;
import com.google.auth.Credentials;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.auth.oauth2.UserCredentials;
import com.google.cloud.storage.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GCSServiceImpl implements GCSService {
    private static final String SSDS_GCS_BUCKET_PATH = "%s/%s/%s/%s/";

    private static final String SSDS_GCS_OBJECT_NAME = "%s_%s.%s";

    @Value("${ssds.gcs.default-gcs-location}")
    private String defaultGCSLocation;

    @Value("${ssds.gcs.project-id}")
    private String projectId;

    @Value("${ssds.gcs.root-bucket-name}")
    private String gcsRootBucketName;

    @Value("${ssds.gcs.impersonated-credentials.target-principal}")
    private String targetPrincipal;

    @Value("${ssds.gcs.impersonated-credentials.life-time:3600}")
    private int impersonatedCredentialsLifeTime;

    @Value("${ssds.gcs.signed-url-time-limit-in-minutes}")
    private Integer signedUrlTimeLimitInMinutes;

    private String getODBucketPath(String attachmentType) {
        ZonedDateTime zdt = ZonedDateTime.now();
        String odBucketPath = String.format(SSDS_GCS_BUCKET_PATH, attachmentType, zdt.getYear(), zdt.getMonthValue(), zdt.getDayOfMonth());
        odBucketPath = odBucketPath.toLowerCase();
        log.debug("The getODBucketPath = {}", odBucketPath);
        return odBucketPath;
    }

    public String getOdGcsObjectName(String fileName) {
        String fileNameWithoutExtension = "";
        String extension = "";
        if (!StringUtils.isEmpty(fileName)) {
            String[] fileNameParts = fileName.split("\\.");
            if (fileNameParts.length > 0) {
                fileNameWithoutExtension = fileNameParts[0];
                if (fileNameParts.length > 1) {
                    extension = fileNameParts[1];
                }
            }
        }
        ZonedDateTime zdt = ZonedDateTime.now();
        String hash = DigestUtils.sha1Hex(fileName + zdt.format(DateTimeFormatter.ofPattern(DateUtils.STR_DEFAULT_TIME_FORMAT)));
        String odBucketName = String.format(SSDS_GCS_OBJECT_NAME, fileNameWithoutExtension, hash, extension);
        log.debug("The getOdGcsObjectName = {}", odBucketName);
        return odBucketName;
    }

    @Override
    public URL download(String jsonMetadata) {
        SSDSStorageDTO wesStorageDTO = JsonSupport.toObject(jsonMetadata, SSDSStorageDTO.class);
        return this.signUrl(wesStorageDTO.getBucketName(), wesStorageDTO.getObjectName(), wesStorageDTO.getProjectId());
    }

    @Override
    public SSDSStorageDTO uploadImageFile(String filename, byte[] fileData, String attachmentType) {
        SSDSStorageDTO ssdsStorageDTO = new SSDSStorageDTO();
        ssdsStorageDTO.setUploadStatus(UploadStatus.FAILED);

        String gcsFilename = getODBucketPath(attachmentType) + getOdGcsObjectName(filename); // i.e. proof_of_delivery/yyyy/mm/dd/filename_hash.png
        Blob result = uploadImageFile(projectId, gcsRootBucketName, gcsFilename, fileData);
        if (null != result) {
            ssdsStorageDTO.setUploadStatus(UploadStatus.SUCCESS);
            ssdsStorageDTO.setStorageSource(StorageSource.GCS);
            ssdsStorageDTO.setBucketName(gcsRootBucketName);
            ssdsStorageDTO.setObjectName(gcsFilename);
            ssdsStorageDTO.setProjectId(projectId);
        }
        return ssdsStorageDTO;
    }

    public Blob uploadImageFile(String projectId, String bucketName, String filename, byte[] fileData) {
        if (!ImageUtils.isImage(fileData)) {
            return null;
        }
        return uploadObject(projectId, bucketName, filename, fileData);
    }

    @Override
    public Blob downloadObject(String projectId, String bucketName, String objectName) {
        Storage storage = getStorage(projectId);
        log.debug("Downloaded object = {} from bucket name = {}", objectName, bucketName);
        return storage.get(BlobId.of(bucketName, objectName));
    }

    @Override
    public boolean deleteObject(String projectId, String bucketName, String objectName) {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
        log.info("Object = {} was deleted from = {}", objectName, bucketName);
        return storage.delete(bucketName, objectName);
    }

    @Override
    public SSDSStorageDTO uploadFile(String filename, byte[] fileData, String attachmentType) {
        SSDSStorageDTO ssdsStorageDTO = new SSDSStorageDTO();
        ssdsStorageDTO.setUploadStatus(UploadStatus.FAILED);

        String gcsFilename = getODBucketPath(attachmentType) + getOdGcsObjectName(filename); // i.e. proof_of_delivery/yyyy/mm/dd/filename_hash.png
        Blob result = uploadObject(projectId, gcsRootBucketName, gcsFilename, fileData);
        if (null != result) {
            ssdsStorageDTO.setUploadStatus(UploadStatus.SUCCESS);
            ssdsStorageDTO.setStorageSource(StorageSource.GCS);
            ssdsStorageDTO.setBucketName(gcsRootBucketName);
            ssdsStorageDTO.setObjectName(gcsFilename);
            ssdsStorageDTO.setProjectId(projectId);
        }
        return ssdsStorageDTO;
    }

    @Override
    public SSDSStorageDTO uploadImageFileToPublicBucket(String filename, byte[] fileData, String attachmentType) {
        SSDSStorageDTO ssdsStorageDTO = new SSDSStorageDTO();
        ssdsStorageDTO.setUploadStatus(UploadStatus.FAILED);

        String gcsFilename = getODBucketPath(attachmentType) + getOdGcsObjectName(filename); // i.e. proof_of_delivery/yyyy/mm/dd/filename_hash.png
        Blob result = uploadImageFile(projectId, gcsRootBucketName, gcsFilename, fileData);
        if (null != result) {
            ssdsStorageDTO.setUploadStatus(UploadStatus.SUCCESS);
            ssdsStorageDTO.setStorageSource(StorageSource.GCS);
            ssdsStorageDTO.setBucketName(gcsRootBucketName);
            ssdsStorageDTO.setObjectName(gcsFilename);
            ssdsStorageDTO.setProjectId(projectId);
            ssdsStorageDTO.setHttp("https://");
        }
        return ssdsStorageDTO;
    }

    private URL signUrl(String bucketName, String blobName, String projectId) {
        Storage storage = getStorage(projectId);
        Credentials credentialsToSign = storage.getOptions().getCredentials();

        if (credentialsToSign instanceof UserCredentials) {
            credentialsToSign = ImpersonatedCredentials.create(
                (GoogleCredentials) credentialsToSign, targetPrincipal, null, Arrays.asList(), impersonatedCredentialsLifeTime);
        }

        // Define resource
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName)).build();
        URL signedUrl = storage.signUrl(blobInfo, signedUrlTimeLimitInMinutes, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature(),
            Storage.SignUrlOption.signWith((ServiceAccountSigner) credentialsToSign));

        log.debug("Signed URL = {}", signedUrl);
        return signedUrl;
    }

    @Override
    public Blob uploadObject(String projectId, String bucketName, String objectName, byte[] fileData) {
        Storage storage = getStorage(projectId);
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(IOUtils.getContentType(new ByteArrayInputStream(fileData)))
            .build();
        log.debug("File is uploading to bucket = {} as = {}", bucketName, objectName);
        return storage.create(blobInfo, fileData);
    }

    @Override
    public Storage getStorage(String projectId) {
        return StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    }
}
