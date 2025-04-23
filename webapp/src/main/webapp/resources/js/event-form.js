/**
 * Event Creation Form with overridden native date validation
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing event creation form with overridden date validation...")

    // Import necessary modules
    const ListAutocomplete = window.ListAutocomplete || {}
    const FileUpload = window.FileUpload || {}

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
            apiEndpoint: "/api/cities/search",
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
    setupDateInput()

    // Form validation
    if (form) {
        // Add real-time validation for date and number fields
        setupRealTimeValidation()

        form.addEventListener("submit", (e) => {
            // Always prevent default to handle validation ourselves
            e.preventDefault()

            // Collect all validation errors at once
            const errors = validateAllFields()

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
            }
        })
    }

    /**
     * Sets up enhanced date input handling
     */
    function setupDateInput() {
        const dateField = document.getElementById("date")
        if (dateField) {
            // Create a hidden input to store the actual date value
            const hiddenDateInput = document.createElement("input")
            hiddenDateInput.type = "hidden"
            hiddenDateInput.name = dateField.name
            hiddenDateInput.id = `${dateField.id}_hidden`
            dateField.parentNode.appendChild(hiddenDateInput)

            // Remove the name attribute from the visible date field to prevent it from being submitted
            // This ensures only our validated value gets submitted
            const originalName = dateField.name
            dateField.removeAttribute("name")

            // Set min attribute to today to prevent selecting past dates in the date picker
            // (This is just a UI enhancement, we'll still validate it in JavaScript)
            const today = new Date()
            const yyyy = today.getFullYear()
            const mm = String(today.getMonth() + 1).padStart(2, '0')
            const dd = String(today.getDate()).padStart(2, '0')
            const todayString = `${yyyy}-${mm}-${dd}`
            dateField.setAttribute("min", todayString)

            // Handle date changes
            dateField.addEventListener("change", () => {
                if (dateField.value) {
                    // Validate the date
                    const isValid = validateDateField(dateField)
                    if (isValid) {
                        // If valid, update the hidden input
                        hiddenDateInput.value = dateField.value
                    } else {
                        // If invalid, clear the hidden input
                        hiddenDateInput.value = ""
                    }
                } else {
                    // If empty, clear the hidden input
                    hiddenDateInput.value = ""
                }
            })

            // Initialize with current value if any
            if (dateField.value) {
                hiddenDateInput.value = dateField.value
            }
        }
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

        // Validate date is not in the past
        const dateField = document.getElementById("date")
        if (dateField) {
            if (!dateField.value.trim()) {
                // Check if date is required
                const dateLabel = document.querySelector(`label[for="${dateField.id}"]`)
                if (dateLabel && dateLabel.classList.contains("required-field")) {
                    errors.push({
                        field: dateField,
                        message: document.getElementById("i18n-required-field")
                            ? document.getElementById("i18n-required-field").value
                            : "This field is required",
                    })
                }
            } else {
                // Validate date format and value
                const dateValue = dateField.value
                const dateRegex = /^\d{4}-\d{2}-\d{2}$/

                if (!dateRegex.test(dateValue)) {
                    errors.push({
                        field: dateField,
                        message: document.getElementById("i18n-invalid-date-format")
                            ? document.getElementById("i18n-invalid-date-format").value
                            : "Please enter a valid date in YYYY-MM-DD format",
                    })
                } else {
                    // Check if date is in the past
                    const selectedDate = new Date(dateValue)
                    selectedDate.setHours(0, 0, 0, 0)

                    const today = new Date()
                    today.setHours(0, 0, 0, 0)

                    if (selectedDate < today) {
                        errors.push({
                            field: dateField,
                            message: document.getElementById("i18n-past-date-error")
                                ? document.getElementById("i18n-past-date-error").value
                                : "Date cannot be in the past",
                        })
                    }
                }
            }
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
        // Real-time validation for date field
        const dateField = document.getElementById("date")
        if (dateField) {
            dateField.addEventListener("change", () => {
                validateDateField(dateField)
            })

            // Also validate on blur to catch manual edits
            dateField.addEventListener("blur", () => {
                validateDateField(dateField)
            })
        }

        // Real-time validation for attendees limit field
        const attendeesLimitField = document.getElementById("attendeesLimit")
        if (attendeesLimitField) {
            attendeesLimitField.addEventListener("input", () => {
                validateAttendeesLimitField(attendeesLimitField)
            })
        }
    }

    /**
     * Validates the date field in real-time
     * @param {HTMLElement} dateField The date input field
     * @returns {boolean} Whether the date is valid
     */
    function validateDateField(dateField) {
        // Remove previous error
        const existingError = dateField.parentNode.querySelector(".error-message")
        if (existingError) {
            existingError.remove()
        }

        // If empty, no validation needed
        if (!dateField.value.trim()) {
            return true
        }

        // Validate date format
        const dateValue = dateField.value
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/

        // if (!dateRegex.test(dateValue)) {
        //
        //     const errorMsg = document.createElement("div")
        //     errorMsg.className = "error-message"
        //     errorMsg.textContent = document.getElementById("i18n-invalid-date-format")
        //         ? document.getElementById("i18n-invalid-date-format").value
        //         : "Please enter a valid date in YYYY-MM-DD format"
        //
        //     dateField.parentNode.appendChild(errorMsg)
        //     return false
        // }

        // Validate date is not in the past
        const selectedDate = new Date(dateValue)
        selectedDate.setHours(0, 0, 0, 0)

        const today = new Date()
        today.setHours(0, 0, 0, 0)

        if (selectedDate < today) {

            const errorMsg = document.createElement("div")
            errorMsg.className = "error-message"
            errorMsg.textContent = document.getElementById("i18n-past-date-error")
                ? document.getElementById("i18n-past-date-error").value
                : "Date cannot be in the past"

            dateField.parentNode.appendChild(errorMsg)
            return false
        }

        return true
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

