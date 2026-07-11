package ar.edu.itba.paw.webapp.utils;

import ar.edu.itba.paw.models.exceptions.InvalidImageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ImageUtils {
    private ImageUtils() {
            throw new AssertionError("Utility class should not be instantiated");
        }
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtils.class);

    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_WEBP = "image/webp";

    public static final long MAX_IMAGE_BYTES = 2L * 1024 * 1024;


    public static String detectContentType(final byte[] data) {
        final String detected = detectImageTypeOrNull(data);
        return detected != null ? detected : IMAGE_JPEG;
    }

    private static String detectImageTypeOrNull(final byte[] data) {
        if (data == null || data.length < 12) {
            return null;
        }
        // PNG: 89 50 4E 47
        if ((data[0] & 0xFF) == 0x89 && data[1] == 0x50 && data[2] == 0x4E && data[3] == 0x47) {
            return IMAGE_PNG;
        }
        // JPEG: FF D8 FF
        if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xFF) == 0xD8 && (data[2] & 0xFF) == 0xFF) {
            return IMAGE_JPEG;
        }
        // WEBP: "RIFF"...."WEBP"
        if (data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
                && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return IMAGE_WEBP;
        }
        return null;
    }

    public static byte[] readImage(final InputStream in) {
        if (in == null) {
            throw new InvalidImageException("exception.image.required");
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final byte[] chunk = new byte[8192];
        long total = 0;
        try {
            int read;
            while ((read = in.read(chunk)) != -1) {
                total += read;
                if (total > MAX_IMAGE_BYTES) {
                    throw new InvalidImageException("exception.image.tooLarge");
                }
                buffer.write(chunk, 0, read);
            }
        } catch (IOException e) {
            LOGGER.error("Error reading uploaded image: {}", e.getMessage());
            throw new InvalidImageException("exception.image.readFailed");
        }
        final byte[] data = buffer.toByteArray();
        if (data.length == 0) {
            throw new InvalidImageException("exception.image.required");
        }
        if (detectImageTypeOrNull(data) == null) {
            throw new InvalidImageException("exception.image.invalidType");
        }
        return data;
    }
}
