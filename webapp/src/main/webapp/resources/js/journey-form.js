/**
 * Journey Creation Form Initialization
 *
 * Initializes and coordinates all components for the journey creation form
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing journey creation form components...")

    // Import necessary modules
    const ListAutocomplete = window.ListAutocomplete || {}
    const DateValidation = window.DateValidation || {}

    const startDateField = document.getElementById("startDate");
    const endDateField = document.getElementById("endDate");

    // Initialize university autocomplete with single-select mode
    try {
        // Check if all required elements exist
        const requiredElements = [
            { id: "destinationUniversity", name: "Select element" },
            { id: "universitySearch", name: "Search input" },
            { id: "universityDropdown", name: "Dropdown container" },
            { id: "selectedUniversities", name: "Selected container" },
        ]

        const missingElements = []
        requiredElements.forEach((el) => {
            if (!document.getElementById(el.id)) {
                missingElements.push(el.name + " (" + el.id + ")")
            }
        })

        if (missingElements.length > 0) {
            console.error("Missing required elements for autocomplete:", missingElements.join(", "))
            return
        }

        // Make sure the dropdown container has a parent with position: relative
        const dropdownContainer = document.getElementById("universityDropdown")
        const autocompleteWrapper = dropdownContainer.closest(".autocomplete-wrapper")
        if (autocompleteWrapper) {
            autocompleteWrapper.style.position = "relative"
        }

        // Add appropriate classes to match design
        const searchInput = document.getElementById("universitySearch")
        if (searchInput) {
            searchInput.classList.add("autocomplete-input")
        }

        if (dropdownContainer) {
            dropdownContainer.classList.add("autocomplete-dropdown")
        }

        const selectedContainer = document.getElementById("selectedUniversities")
        if (selectedContainer) {
            selectedContainer.classList.add("selected-tags")
        }
        const emptyMessage = document.getElementById("i18n-university-none")
            ? document.getElementById("i18n-university-none").value
            : "No university selected"

        window.universityAutocomplete = ListAutocomplete.init({
            selectId: "destinationUniversity",
            searchId: "universitySearch",
            dropdownId: "universityDropdown",
            selectedContainerId: "selectedUniversities",
            emptyMessage: emptyMessage,
            multiSelect: false, // Single-select mode
            onSelect: (value, text) => {
                console.log(`Selected university: ${text} (${value})`)
                // Force update the select element value
                const selectElement = document.getElementById("destinationUniversity")
                if (selectElement) {
                    // For single-select, just set the value
                    selectElement.value = value

                    // Trigger change event
                    const event = new Event("change", { bubbles: true })
                    selectElement.dispatchEvent(event)
                }
            },
            onRemove: (value) => {
                console.log(`Removed university: ${value}`)
            },
            error: document.getElementById("destinationUniversity.errors") !== null,
        })
        console.log("University autocomplete component initialized")

        // Remove any inline styles we added previously
        const oldStyle = document.getElementById("autocomplete-inline-styles")
        if (oldStyle) {
            oldStyle.remove()
        }
    } catch (error) {
        console.error("Failed to initialize university autocomplete component:", error)
    }

    try {
        DateValidation.init(startDateField);
        DateValidation.init(endDateField);
    } catch (error) {
        console.error("Failed to initialize date validation component:", error)
    }

    // Form validation
        const form = document.getElementById("journeyForm");
        if (form) {
            form.addEventListener("submit", (e) => {
                e.preventDefault(); // Prevent default form submission

                /*const errors = validateAllFields();

                if (errors.length > 0) {
                    displayErrors(errors);

                    // Scroll to the first error
                    const firstErrorField = errors[0].field;
                    firstErrorField.scrollIntoView({ behavior: "smooth", block: "center" });
                    firstErrorField.focus();
                } else {
                    form.submit(); // Submit the form if no errors
                }*/
                form.submit();
            });
        }

        /**
         * Validates all form fields and returns an array of errors
         * @returns {Array} Array of error objects with field and message properties
         */
        function validateAllFields() {
            const errors = [];

            // Validate required fields
            const requiredFields = form.querySelectorAll(
                "[required], .required-field + .form-input, .required-field + .form-textarea, .required-field + .autocomplete-wrapper select"
            );
            requiredFields.forEach((field) => {
                if (!field.value.trim()) {
                    errors.push({
                        field: field,
                        message: document.getElementById("i18n-required-field")
                            ? document.getElementById("i18n-required-field").value
                            : "This field is required",
                    });
                }
            });

            // Validate date range

            const startDateValidation = new DateValidation.isValid(startDateField)
            if (!startDateValidation.isValid) {
                errors.push({
                    field: startDateField,
                    message: startDateValidation.error
                });
            }

            const endDateValidation = new DateValidation.isValid(endDateField)
            if (!endDateValidation.isValid) {
                errors.push({
                    field: endDateField,
                    message: endDateValidation.error
                });
            }

            if (!startDateValidation.isValid || !endDateValidation.isValid){
                return errors;
            }

            //Selecting again to ensure actual value if date validator used
            const startDate = document.getElementsByName("startDate").item(0);
            const endDate = document.getElementsByName("endDate").item(0);

            if (startDate && endDate && startDate.value && endDate.value) {
                if (new Date(startDate.value) >= new Date(endDate.value)) {
                    errors.push({
                        field: endDate,
                        message: document.getElementById("i18n-valid-date-error")
                            ? document.getElementById("i18n-valid-date-error").value
                            : "End date must be after start date",
                    });
                }
            }

            return errors;
        }

        /**
         * Displays all validation errors
         * @param {Array} errors Array of error objects with field and message properties
         */
        function displayErrors(errors) {
            // Clear previous error messages
            clearErrorMessages();

            // Display new error messages
            errors.forEach((error) => {
                const field = error.field;
                const message = error.message;

                // Create error message element
                const errorMsg = document.createElement("div");
                errorMsg.className = "error-message";
                errorMsg.textContent = message;

                // Append error message to the field's parent container
                const parent = field.parentNode;
                if (!parent.querySelector(".error-message")) {
                    parent.appendChild(errorMsg);
                }
            });
        }

        /**
         * Clears all error messages
         */
        function clearErrorMessages() {
            const errorMessages = document.querySelectorAll(".error-message");
            errorMessages.forEach((msg) => msg.remove());
        }

        console.log("Journey creation form initialization complete");
    });