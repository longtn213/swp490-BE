package com.fpt.ssds.web.rest.publicapi;

import com.fpt.ssds.service.SSDSStorageService;
import com.fpt.ssds.service.dto.FileDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${ssds.api.ref.public}/web/v1/file")
@Slf4j
public class FileController {

    @Autowired
    private SSDSStorageService ssdsStorageService;

    @PostMapping("upload-image/{type}")
    public ResponseEntity<ResponseDTO> uploadImage(@PathVariable String type, @RequestParam MultipartFile file) {
        ssdsStorageService.verifyUploadAttachment(file, type);
        FileDto fileDTOS = ssdsStorageService.uploadFileToImgur(file, type);
//        FileDto fileDTOS = ssdsStorageService.uploadFile(file, type);
        return ResponseEntity.ok().body(ResponseUtils.responseOK(fileDTOS));
    }
}
