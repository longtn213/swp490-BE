package com.fpt.ssds.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class IOUtils extends org.apache.commons.io.IOUtils {
    private static final Logger log = LoggerFactory.getLogger(IOUtils.class);

    public static String getContentType(InputStream is) {
        String contentType = null;
        if (is != null) {
            try {
                contentType = URLConnection.guessContentTypeFromStream(is);
            } catch (IOException e) {
                log.error("Error during get content type form input stream", e);
            }
        }
        return contentType;
    }
}
