package com.fpt.ssds.service;

import com.fpt.ssds.service.dto.ResponseDTO;

public interface CommonService {
    ResponseDTO getSelectionByType(String type);

//    FileDTO uploadImage(String fileType, MultipartFile file);
}
