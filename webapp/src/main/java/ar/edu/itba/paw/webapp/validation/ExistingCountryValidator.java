package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.interfaces.services.CountryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExistingCountryValidator implements ConstraintValidator<ExistingCountry, String> {

    private final CountryService countryService;
    @Autowired
    public ExistingCountryValidator(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void initialize(ExistingCountry constraintAnnotation) {
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        if (country == null || country.isEmpty()) {
            return true;
        }

        return countryService.findCountryByName(country).isPresent();
    }
}
