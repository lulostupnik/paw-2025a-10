
/**
 * Sets up enhanced date input handling
 */
let DateValidation = (() => {
    function init(dateField) {
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

            // Initialize with current value if any
            if (dateField.value) {
                hiddenDateInput.value = dateField.value
            }

            // Handle date changes
            dateField.addEventListener("change", () => {
                // Remove previous error
                const existingError = dateField.parentNode.querySelector(".error-message")
                if (existingError) {
                    existingError.remove()
                }

                if (dateField.value) {
                    // Validate the date
                    const {isValid, error} = validateDateField(dateField)
                    if (isValid) {
                        // If valid, update the hidden input
                        hiddenDateInput.value = dateField.value
                    } else {
                        // If invalid, clear the hidden input
                        hiddenDateInput.value = ""

                        // Create error message element
                        const errorMsg = document.createElement("div")
                        errorMsg.className = "error-message"
                        errorMsg.textContent = error

                        // Find the parent container for the field
                        const parent = dateField.parentNode

                        // Check if there's already an error message
                        const existingError = parent.querySelector(".error-message")
                        if (!existingError) {
                            parent.appendChild(errorMsg)
                        }
                    }
                } else {
                    // If empty, clear the hidden input
                    hiddenDateInput.value = ""
                }
            })
        }
    }
        
    /**
     * Validates the date field in real-time
     * @param {HTMLElement} dateField The date input field
     * @returns {(boolean, string)} Whether the date is valid, plus error message
     */
    function validateDateField(dateField) {
        // If empty and is required
        if (!dateField.value.trim()) {
            // Check if date is required
            const dateLabel = document.querySelector(`label[for="${dateField.id}"]`)
            if (dateLabel && dateLabel.classList.contains("required-field")) {
                return{
                    isValid: false, error: document.getElementById("i18n-required-field")
                        ? document.getElementById("i18n-required-field").value
                        : "This field is required"
                }
            }
            return {isValid: true, error: ""};
        }

        // Validate date format
        const dateValue = dateField.value
        const dateRegex = /^\d{4}-\d{2}-\d{2}$/

        if (!dateRegex.test(dateValue)) {
            return {isValid: false, error: document.getElementById("i18n-invalid-date-format")
                ? document.getElementById("i18n-invalid-date-format").value
                : "Please enter a valid date in YYYY-MM-DD format"}
        }

        // Validate date is not in the past
        const selectedDate = new Date(dateValue)
        selectedDate.setHours(23, 59, 59, 0)

        const today = new Date()
        today.setHours(0, 0, 0, 0)

        if (selectedDate < today) {
            return {isValid: false, error: document.getElementById("i18n-past-date-error")
                ? document.getElementById("i18n-past-date-error").value
                : "Date cannot be in the past"}
        }

        return {isValid: true, error: ""};
    }
    
    // Public API
    return {
        init: init,
        isValid: validateDateField
    }
})()

// Make available globally
window.DateValidation = DateValidation