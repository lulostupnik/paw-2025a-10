/**
 * Event Creation Form Initialization
 *
 * Initializes and coordinates all components for the event creation form
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing event creation form components...")

    // Import necessary modules
    const ListAutocomplete = window.ListAutocomplete || {}
    const FileUpload = window.FileUpload || {}

    // Initialize city autocomplete with single-select mode
    try {
        window.eventCityAutocomplete = ListAutocomplete.init({
            selectId: "city",
            searchId: "citySearch",
            dropdownId: "cityDropdown",
            selectedContainerId: "selectedCity",
            apiEndpoint: "/api/cities/search", // Add API endpoint if available
            minChars: 2,
            debounceTime: 300,
            emptyMessage: "No city selected",
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

    // Form validation
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

            // Validate event date is not in the past
            const eventDate = document.getElementById("date")
            if (eventDate && eventDate.value) {
                const today = new Date()
                today.setHours(0, 0, 0, 0)

                if (new Date(eventDate.value) < today) {
                    isValid = false
                    eventDate.classList.add("error")

                    let errorMsg = eventDate.parentNode.querySelector(".error-message")
                    if (!errorMsg) {
                        errorMsg = document.createElement("div")
                        errorMsg.className = "error-message"
                        eventDate.parentNode.appendChild(errorMsg)
                    }
                    errorMsg.textContent = "Event date cannot be in the past"
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

    console.log("Event creation form initialization complete")
})
