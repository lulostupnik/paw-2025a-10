/**
 * Registration Page Initialization
 *
 * Initializes and coordinates all components for the registration page
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing registration components...")

    // Import necessary modules (assuming they are available)
    // If not using modules, ensure these variables are declared globally or loaded via script tags
    const PasswordStrength = window.PasswordStrength || {}
    const Autocomplete = window.Autocomplete || {}
    const ListAutocomplete = window.ListAutocomplete || {}
    const FileUpload = window.FileUpload || {}

    // Initialize password strength component
    try {
        window.passwordStrength = PasswordStrength.init({
            // Using default IDs
        })
        console.log("Password strength component initialized")
    } catch (error) {
        console.error("Failed to initialize password strength component:", error)
    }

    // Initialize career autocomplete
    try {
        window.careerAutocomplete = Autocomplete.init({
            selectId: "career",
            searchId: "careerSearch",
            dropdownId: "careerDropdown",
        })
        console.log("Career autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize career autocomplete component:", error)
    }

    // Initialize university autocomplete
    try {
        window.universityAutocomplete = Autocomplete.init({
            selectId: "originUniversity",
            searchId: "universitySearch",
            dropdownId: "universityDropdown",
        })
        console.log("University autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize university autocomplete component:", error)
    }

    // Initialize interests multi-select
    try {
        const emptyMessage = document.getElementById("i18n-interests-none")
            ? document.getElementById("i18n-interests-none").value
            : "No interests selected"

        window.interestsAutocomplete = ListAutocomplete.init({
            selectId: "interestsSelect",
            searchId: "interestSearch",
            dropdownId: "interestDropdown",
            selectedContainerId: "selectedInterests",
            emptyMessage: emptyMessage,
        })
        console.log("Interests list autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize interests list autocomplete component:", error)
    }

    // Initialize file upload
    try {
        window.FileUpload = FileUpload.init({
            // Using default IDs
            maxSizeMB: 5,
            sizeExceededMessage: "File size exceeds 5MB limit",
        })
        console.log("File upload component initialized")
    } catch (error) {
        console.error("Failed to initialize file upload component:", error)
    }

    // Additional form validation
    const form = document.querySelector(".auth-form")
    if (form) {
        form.addEventListener("submit", (e) => {
            let isValid = true

            // Validate required fields
            const requiredFields = form.querySelectorAll("[required]")
            requiredFields.forEach((field) => {
                if (!field.value.trim()) {
                    isValid = false
                    field.classList.add("error")

                    // Create error message if it doesn't exist
                    let errorMsg = field.parentNode.querySelector(".error-message")
                    if (!errorMsg) {
                        errorMsg = document.createElement("div")
                        errorMsg.className = "error-message"
                        field.parentNode.appendChild(errorMsg)
                    }
                    errorMsg.textContent = "This field is required"
                } else {
                    field.classList.remove("error")
                    const errorMsg = field.parentNode.querySelector(".error-message")
                    if (errorMsg) {
                        errorMsg.remove()
                    }
                }
            })

            // Validate email format
            const emailField = form.querySelector('input[type="email"]')
            if (emailField && emailField.value.trim()) {
                const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
                if (!emailPattern.test(emailField.value)) {
                    isValid = false
                    emailField.classList.add("error")

                    // Create error message if it doesn't exist
                    let errorMsg = emailField.parentNode.querySelector(".error-message")
                    if (!errorMsg) {
                        errorMsg = document.createElement("div")
                        errorMsg.className = "error-message"
                        emailField.parentNode.appendChild(errorMsg)
                    }
                    errorMsg.textContent = "Please enter a valid email address"
                }
            }

            if (!isValid) {
                e.preventDefault()
                // Scroll to the first error
                const firstError = form.querySelector(".error")
                if (firstError) {
                    firstError.scrollIntoView({ behavior: "smooth", block: "center" })
                    firstError.focus()
                }
            }
        })
    }

    console.log("Registration initialization complete")
})
