/**
 * Event Creation Form with overridden native date validation
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing event creation form with overridden date validation...")

    // Import necessary modules
    const ListAutocomplete = window.ListAutocomplete || {}
    const FileUpload = window.FileUpload || {}
    const DateValidation = window.DateValidation || {}

    const dateField = document.getElementById("date")

    // Add novalidate attribute to the form to disable browser's native validation
    const form = document.querySelector(".auth-form")
    // if (form) {
    //     form.setAttribute("novalidate", "")
    // }

    // Initialize city autocomplete with single-select mode
    const emptyMessage = document.getElementById("i18n-city-none")
        ? document.getElementById("i18n-city-none").value
        : "No city selected"

    try {
        window.eventCityAutocomplete = ListAutocomplete.init({
            selectId: "city",
            searchId: "citySearch",
            dropdownId: "cityDropdown",
            selectedContainerId: "selectedCity",
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Single-select mode
            error: document.getElementById("city.errors") !== null,
        })
        console.log("City autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize city autocomplete component:", error)
    }

    // Initialize file upload component
    try {
        window.eventFileUpload = FileUpload.init({
            fileInputId: "eventFile",
            previewContainerId: "filePreview",
            previewImageId: "previewImage",
            fileNameId: "fileName",
            removeButtonId: "removeFile",
            maxSizeMB: 5,
            sizeExceededMessage: "File size exceeds 5MB limit",
        })
        console.log("File upload component initialized")
    } catch (error) {
        console.error("Failed to initialize file upload component:", error)
    }

    // Enhanced date input handling
    DateValidation.init(dateField)

    // Form validation
    if (form) {
        // Add real-time validation for date and number fields
        setupRealTimeValidation()

        form.addEventListener("submit", (e) => {
            // Always prevent default to handle validation ourselves
            e.preventDefault()

            // Collect all validation errors at once
            /*const errors = validateAllFields()

            // If there are errors, display all errors
            if (errors.length > 0) {
                displayErrors(errors)

                // Scroll to the first error
                if (errors.length > 0) {
                    const firstErrorField = errors[0].field
                    firstErrorField.scrollIntoView({ behavior: "smooth", block: "center" })
                    firstErrorField.focus()
                }
            } else {
                // If no errors, manually submit the form
                form.submit()
            }*/
            form.submit()
        })
    }

    /**
     * Validates all form fields and returns an array of errors
     * @returns {Array} Array of error objects with field and message properties
     */
    function validateAllFields() {
        const errors = []

        // Validate required fields
        const requiredFields = form.querySelectorAll(
            "[required], .required-field + .form-input, .required-field + .form-textarea, .required-field + .autocomplete-wrapper select"
        )
        requiredFields.forEach((field) => {
            // Skip fields that are disabled (like when "all day event" is checked)
            if (field.disabled) return

            if (!field.value.trim()) {
                errors.push({
                    field: field,
                    message: document.getElementById("i18n-required-field")
                        ? document.getElementById("i18n-required-field").value
                        : "This field is required",
                })
            }
        })

        // Validate date
        const dateValidation = new DateValidation.isValid(dateField)
        if (!dateValidation.isValid) {
            errors.push({
                field: dateField,
                message: dateValidation.error
            })
        }

        // Validate attendees limit is a positive number
        const attendeesLimitField = document.getElementById("attendeesLimit")
        const noAttendeesLimit = document.querySelector("input[name='noAttendeesLimit']")

        if (attendeesLimitField && !attendeesLimitField.disabled && attendeesLimitField.value) {
            const attendeesLimit = Number.parseInt(attendeesLimitField.value, 10)

            if (isNaN(attendeesLimit) || attendeesLimit <= 0) {
                errors.push({
                    field: attendeesLimitField,
                    message: document.getElementById("i18n-positive-number-error")
                        ? document.getElementById("i18n-positive-number-error").value
                        : "Please enter a positive number",
                })
            }
        }

        // Validate city is selected (for autocomplete fields)
        const cityField = document.getElementById("city")
        if (cityField && !cityField.value.trim()) {
            const citySearchField = document.getElementById("citySearch")
            if (citySearchField) {
                errors.push({
                    field: citySearchField,
                    message: document.getElementById("i18n-city-required")
                        ? document.getElementById("i18n-city-required").value
                        : "Please select a city",
                })
            }
        }

        return errors
    }

    /**
     * Displays all validation errors
     * @param {Array} errors Array of error objects with field and message properties
     */
    function displayErrors(errors) {
        // Clear previous error messages
        clearErrorMessages()

        // Display new error messages
        errors.forEach((error) => {
            const field = error.field
            const message = error.message


            // Create error message element
            const errorMsg = document.createElement("div")
            errorMsg.className = "error-message"
            errorMsg.textContent = message

            // Find the parent container for the field
            const parent = field.parentNode

            // For autocomplete fields, we need to go up one more level
            if (parent.classList.contains("autocomplete-wrapper")) {
                parent.appendChild(errorMsg)
            } else {
                // Check if there's already an error message
                const existingError = parent.querySelector(".error-message")
                if (!existingError) {
                    parent.appendChild(errorMsg)
                }
            }
        })
    }

    /**
     * Clears all error messages
     */
    function clearErrorMessages() {
        // Remove error class from all fields
        const errorFields = document.querySelectorAll(".error")
        errorFields.forEach((field) => {
        })

        // Remove all client-side error messages
        const errorMessages = document.querySelectorAll(".error-message:not([data-server-error])")
        errorMessages.forEach((msg) => {
            msg.remove()
        })
    }

    /**
     * Sets up real-time validation for date and number fields
     */
    function setupRealTimeValidation() {
        // Real-time validation for attendees limit field
        const attendeesLimitField = document.getElementById("attendeesLimit")
        if (attendeesLimitField) {
            attendeesLimitField.addEventListener("input", () => {
                validateAttendeesLimitField(attendeesLimitField)
            })
        }
    }

    /**
     * Validates the attendees limit field in real-time
     * @param {HTMLElement} attendeesLimitField The attendees limit input field
     */
    function validateAttendeesLimitField(attendeesLimitField) {
        // Skip validation if the field is disabled
        if (attendeesLimitField.disabled) return

        // Remove previous error
        const existingError = attendeesLimitField.parentNode.querySelector(".error-message")
        if (existingError) {
            existingError.remove()
        }

        // Validate attendees limit is a positive number
        if (attendeesLimitField.value) {
            const attendeesLimit = Number.parseInt(attendeesLimitField.value, 10)

            if (isNaN(attendeesLimit) || attendeesLimit <= 0) {

                const errorMsg = document.createElement("div")
                errorMsg.className = "error-message"
                errorMsg.textContent = document.getElementById("i18n-positive-number-error")
                    ? document.getElementById("i18n-positive-number-error").value
                    : "Please enter a positive number"

                attendeesLimitField.parentNode.appendChild(errorMsg)
            }
        }
    }

    console.log("Event creation form initialization complete")
})

