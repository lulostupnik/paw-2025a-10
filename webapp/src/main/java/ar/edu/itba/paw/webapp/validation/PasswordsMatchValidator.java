package ar.edu.itba.paw.webapp.validation;


import ar.edu.itba.paw.webapp.form.CreateUserForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, CreateUserForm> {

    @Override
    public boolean isValid(CreateUserForm form, ConstraintValidatorContext context) {
        if (form.getPassword() == null && form.getConfirmPassword() == null)
            return true;
        if ((form.getPassword() == null && form.getConfirmPassword() != null) || (form.getPassword() != null && form.getConfirmPassword() == null) )
            return false;

        boolean matches = form.getPassword().equals(form.getConfirmPassword());
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }
        return matches;
    }
}
