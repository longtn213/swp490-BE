package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.enumeration.StorageSource;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.utils.JsonSupport;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SSDSStorageDTO {
    @Enumerated(EnumType.STRING)
    private StorageSource storageSource;

    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    private String bucketName;

    private String projectId;

    private String objectName;

    private String failedReason;

    private String http;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }
        final SSDSStorageDTO wesStorageDTO = (SSDSStorageDTO) o;
        return Objects.nonNull(projectId) && projectId.equals(wesStorageDTO.projectId)
            && Objects.nonNull(bucketName) && bucketName.equals(wesStorageDTO.bucketName)
            && Objects.nonNull(objectName) && objectName.equals(wesStorageDTO.objectName);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return JsonSupport.toJson(this);
    }
}
