package com.fpt.ssds.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImgurService {

    String uploadFile(MultipartFile multipartFile);
}
