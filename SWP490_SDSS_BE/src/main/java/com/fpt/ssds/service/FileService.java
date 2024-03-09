package com.fpt.ssds.service;

import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.service.dto.FileDto;

import java.util.List;

public interface FileService {
    void updateFileRefId(List<FileDto> files, Long refId);

    List<FileDto> findByTypeAndRefIdAndUploadStatus(FileType fileType, Long refId, UploadStatus uploadStatus);
}
