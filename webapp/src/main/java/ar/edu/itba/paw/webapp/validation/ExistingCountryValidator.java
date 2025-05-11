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
        // No initialization needed
    }

    @Override
    public boolean isValid(String country, ConstraintValidatorContext context) {
        if (country == null || country.isEmpty()) {
            return true;
        }
        // Implement the logic to check if the university exists in the database
        // For example:
        // return universityService.existsByName(universityName);
        return countryService.findByName(country).isPresent(); // Placeholder, replace with actual logic
    }
}
