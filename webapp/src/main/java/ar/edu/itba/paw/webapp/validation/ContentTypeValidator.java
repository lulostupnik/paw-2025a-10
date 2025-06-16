package ar.edu.itba.paw.webapp.validation;

import org.springframework.web.multipart.MultipartFile;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ContentTypeValidator implements ConstraintValidator<ContentType, MultipartFile> {

    private String[] allowedContentTypes;

    @Override
    public void initialize(ContentType constraintAnnotation) {
        this.allowedContentTypes = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Let @NotNull handle null validation
        }

        String contentType = file.getContentType();
        return contentType != null && Arrays.asList(allowedContentTypes).contains(contentType);
    }
}