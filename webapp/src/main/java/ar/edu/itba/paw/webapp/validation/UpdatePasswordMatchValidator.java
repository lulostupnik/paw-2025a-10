package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.webapp.form.CreateUserForm;
import ar.edu.itba.paw.webapp.form.UpdatePasswordForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdatePasswordMatchValidator implements ConstraintValidator<PasswordsMatch, UpdatePasswordForm> {

    @Override
    public boolean isValid(UpdatePasswordForm form, ConstraintValidatorContext context) {
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
        //return form.getPassword().equals(form.getConfirmPassword());
    }
}
