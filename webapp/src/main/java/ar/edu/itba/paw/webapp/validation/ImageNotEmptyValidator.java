package ar.edu.itba.paw.webapp.validation;

import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;

public class ImageNotEmptyValidator implements ConstraintValidator<ImageNotEmpty, MultipartFile> {
    private long max;
    @Override
    public void initialize(ImageNotEmpty constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile file, javax.validation.ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        return file.getSize() > 0;
    }
}
