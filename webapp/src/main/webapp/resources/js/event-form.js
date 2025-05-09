/**
 * Event Creation Form with overridden native date validation
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing event creation form with overridden date validation...")

    // Import necessary modules
    const ListAutocomplete = window.SingleOptionAutocomplete || {}
    const FileUpload = window.FileUpload || {}
    const DateValidation = window.DateValidation || {}

    const dateField = document.getElementById("date")

    // Add novalidate attribute to the form to disable browser's native validation
    const form = document.querySelector(".auth-form")
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
            apiEndpoint: `${apiBaseUrl}cities`,
            selectedValue: eventSelectedCity,
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
            form.submit()
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

