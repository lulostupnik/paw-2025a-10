document.addEventListener('DOMContentLoaded', function() {
    // Initialize the city autocomplete
    initializeAutocomplete('citySearch', 'cityDropdown', 'city', 'selectedCities', false);

    // Form validation
    const universityForm = document.getElementById('universityForm');
    if (universityForm) {
        universityForm.addEventListener('submit', function(event) {
            let isValid = true;

            // Validate name
            const nameInput = document.getElementById('name');
            if (!nameInput.value.trim()) {
                markFieldAsInvalid(nameInput);
                isValid = false;
            } else {
                markFieldAsValid(nameInput);
            }

            // Validate abbreviation
            const abbreviationInput = document.getElementById('abbreviation');
            if (!abbreviationInput.value.trim()) {
                markFieldAsInvalid(abbreviationInput);
                isValid = false;
            } else {
                markFieldAsValid(abbreviationInput);
            }

            // Validate city
            const citySelect = document.getElementById('city');
            if (!citySelect.value) {
                const citySearch = document.getElementById('citySearch');
                markFieldAsInvalid(citySearch);
                isValid = false;
            } else {
                const citySearch = document.getElementById('citySearch');
                markFieldAsValid(citySearch);
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    }

    function markFieldAsInvalid(field) {
        field.classList.add('error');
        const errorMessage = document.createElement('div');
        errorMessage.className = 'error-message';
        errorMessage.textContent = 'This field is required';

        // Remove any existing error message
        const existingError = field.parentNode.querySelector('.error-message');
        if (existingError) {
            field.parentNode.removeChild(existingError);
        }

        field.parentNode.appendChild(errorMessage);
    }

    function markFieldAsValid(field) {
        field.classList.remove('error');
        const existingError = field.parentNode.querySelector('.error-message');
        if (existingError) {
            field.parentNode.removeChild(existingError);
        }
    }
});