package com.fpt.ssds.service.impl;

import com.fpt.ssds.domain.File;
import com.fpt.ssds.domain.enumeration.FileType;
import com.fpt.ssds.domain.enumeration.UploadStatus;
import com.fpt.ssds.repository.FileRepository;
import com.fpt.ssds.service.FileService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    private final FileMapper fileMapper;

    @Override
    public void updateFileRefId(List<FileDto> files, Long refId) {
        List<File> modifiedFiles = fileRepository.findAllById(files.stream().filter(fileDto -> Objects.nonNull(fileDto.getId())).map(FileDto::getId).collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(modifiedFiles)) {
            List<File> oldFiles = fileRepository.findByTypeAndRefId(modifiedFiles.get(0).getType(), refId);
            for (File oldFile : oldFiles) {
                oldFile.setRefId(null);
            }
            for (File file : modifiedFiles) {
                file.setRefId(refId);
            }
            modifiedFiles.addAll(oldFiles);
            fileRepository.saveAll(modifiedFiles);
        }
    }

    @Override
    public List<FileDto> findByTypeAndRefIdAndUploadStatus(FileType fileType, Long refId, UploadStatus uploadStatus) {
        List<File> files = fileRepository.findByTypeAndRefIdAndUploadStatus(fileType, refId, uploadStatus);
        return fileMapper.toDto(files);
    }
}
