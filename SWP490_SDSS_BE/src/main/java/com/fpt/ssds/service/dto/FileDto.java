package com.fpt.ssds.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fpt.ssds.domain.File;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link File} entity
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto implements Serializable {
    private Long id;

    private FileType type;

    private String url;

    private Integer ordinal;

    private UploadStatus uploadStatus;

    private String failedReason;

    private Long refId;
}
