package ar.edu.itba.paw.webapp.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;

public class ImageSizeValidator implements ConstraintValidator<ImageSize, MultipartFile> {
private long max;
    @Override
    public void initialize(ImageSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(MultipartFile file, javax.validation.ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false; //@Todo podria ser otra validacion
        }
        return file.getSize() <= max;
    }
}
