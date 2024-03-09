package com.fpt.ssds.repository;

import com.fpt.ssds.domain.File;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByTypeAndRefId(FileType fileType, Long refId);

    List<File> findByTypeAndRefIdAndUploadStatus(FileType fileType, Long refId, UploadStatus uploadStatus);

    List<File> findByTypeAndRefIdInAndUploadStatus(FileType fileType, List<Long> listRefID, UploadStatus uploadStatus);
}
