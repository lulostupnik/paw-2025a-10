package ar.edu.itba.paw.webapp.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * Custom validator that implements role-based message validation logic.
 * For ADMIN users: message must not be null or empty
 */
@Component
public class RoleBasedMessageValidator implements ConstraintValidator<RoleBasedMessage, String> {

    @Override
    public void initialize(RoleBasedMessage constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Collection<? extends GrantedAuthority> authorities =
                SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        //Should first check if the user.isOwner
        if (isAdmin) {
            return value != null && !value.trim().isEmpty();
        }
        return true;
    }
}
