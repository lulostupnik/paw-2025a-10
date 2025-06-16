package ar.edu.itba.paw.webapp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public final class ImageUtils {
    private ImageUtils() {
            throw new AssertionError("Utility class should not be instantiated");
        }
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);
    public static byte[] getBytes(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            LOGGER.debug("File bytes loaded successfully, size: {} bytes", bytes.length);
            return bytes;
        } catch (Exception e) {
            LOGGER.error("Error reading file bytes: {}", e.getMessage());
            return null;
        }
    }
}
