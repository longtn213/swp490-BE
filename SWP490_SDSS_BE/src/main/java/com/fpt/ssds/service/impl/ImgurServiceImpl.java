package com.fpt.ssds.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.ssds.common.exception.SSDSBusinessException;
import com.fpt.ssds.constant.ErrorConstants;
import com.fpt.ssds.service.ImgurService;
import com.fpt.ssds.service.dto.imgur.TokenResponseDTO;
import com.fpt.ssds.service.dto.imgur.UploadImageResponseDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;

@Slf4j
@Service
public class ImgurServiceImpl implements ImgurService {
    String clientId = "89f8c93a4024c3b"; // from registration
    String clientSecret = "2e8475301af534369c8a70bf4af6fb38fba4a964";
    String refreshToken = "c71e8a0d2d990a9a376ae612a95c8e5bbe478cfa";

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String accessToken = getAccessToken();
        try {
            String encodedString = Base64.getEncoder().encodeToString(multipartFile.getBytes());

            OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", encodedString)
                .addFormDataPart("type", "base64")
                .addFormDataPart("name", Instant.now().toEpochMilli() + ".gif")
                .build();
            Request request = new Request.Builder()
                .url("https://api.imgur.com/3/image")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
            Response response = client.newCall(request).execute();
            ObjectMapper mapper = new ObjectMapper();
            UploadImageResponseDTO responseDTO = mapper.readValue(response.body().string(), UploadImageResponseDTO.class);
            if (!responseDTO.getSuccess()) {
                log.error("Error when uploadFile to imgur");
                throw new SSDSBusinessException(ErrorConstants.FILE_UPLOAD_ERROR);
            }
            return responseDTO.getData().getLink();
        } catch (IOException e) {
            log.error("Error when uploadFile to imgur");
            throw new RuntimeException(e);
        }
    }

    private String getAccessToken() {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("refresh_token", refreshToken)
            .addFormDataPart("client_id", clientId)
            .addFormDataPart("client_secret", clientSecret)
            .addFormDataPart("grant_type", "refresh_token")
            .build();
        Request request = new Request.Builder()
            .url("https://api.imgur.com/oauth2/token")
            .method("POST", body)
            .build();
        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            ObjectMapper mapper = new ObjectMapper();
            TokenResponseDTO tokenResponseDTO = mapper.readValue(responseBody.string(), TokenResponseDTO.class);
            return tokenResponseDTO.getAccessToken();
        } catch (IOException e) {
            log.error("Error when getAccessToken for imgur");
            throw new RuntimeException(e);
        }
    }


}
