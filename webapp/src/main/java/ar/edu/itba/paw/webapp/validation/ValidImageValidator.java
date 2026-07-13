package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.utils.ImageUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidImageValidator implements ConstraintValidator<ValidImage, byte[]> {

    @Override
    public void initialize(ValidImage constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(final byte[] data, final ConstraintValidatorContext context) {
        if (data == null || data.length == 0) {
            return fail(context, "{validation.image.required}");
        }
        if (data.length > ImageUtils.MAX_IMAGE_BYTES) {
            return fail(context, "{validation.image.tooLarge}");
        }
        if (!ImageUtils.isSupportedImageType(data)) {
            return fail(context, "{validation.image.invalidType}");
        }
        return true;
    }

    private boolean fail(final ConstraintValidatorContext context, final String messageTemplate) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
        return false;
    }
}
