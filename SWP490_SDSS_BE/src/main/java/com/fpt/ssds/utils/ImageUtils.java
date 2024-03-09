package com.fpt.ssds.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;

@Slf4j
public class ImageUtils {
    private static final double MEGABYTE = (double) 1024L * 1024L;

    public static boolean isImage(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ImageIO.read(bis).getHeight();
            return true;
        } catch (Exception ex) {
            log.warn("Processing isImage got warning = {}", ex.getMessage());
            return false;
        }
    }

    public static double getImageFileSizeInMB(MultipartFile file) {
        if (null == file) {
            return 0;
        }
        return file.getSize() / MEGABYTE;
    }
}
