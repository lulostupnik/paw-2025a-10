/**
 * Journey Creation Form Initialization
 *
 * Initializes and coordinates all components for the journey creation form
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing journey creation form components...")

    // Import necessary modules
    const ListAutocomplete = window.ListAutocomplete || {}

    // Initialize university autocomplete with multi-select mode
    try {
        window.universityAutocomplete = ListAutocomplete.init({
            selectId: "destinationUniversity",
            searchId: "universitySearch",
            dropdownId: "universityDropdown",
            selectedContainerId: "selectedUniversities",
            emptyMessage: "No universities selected",
            multiSelect: true, // Multi-select mode
            onSelect: (value, text) => {
                console.log(`Selected university: ${text} (${value})`)
            },
            onRemove: (value) => {
                console.log(`Removed university: ${value}`)
            },
        })
        console.log("University autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize university autocomplete component:", error)
    }

    // Form validation
    const form = document.getElementById("journeyForm")
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

            // Validate date range
            const startDate = document.getElementById("startDate")
            const endDate = document.getElementById("endDate")
            if (startDate && endDate && startDate.value && endDate.value) {
                if (new Date(startDate.value) > new Date(endDate.value)) {
                    isValid = false
                    endDate.classList.add("error")

                    let errorMsg = endDate.parentNode.querySelector(".error-message")
                    if (!errorMsg) {
                        errorMsg = document.createElement("div")
                        errorMsg.className = "error-message"
                        endDate.parentNode.appendChild(errorMsg)
                    }
                    errorMsg.textContent = "End date must be after start date"
                }
            }

            // Validate university selection
            if (window.universityAutocomplete) {
                const selectedUniversities = window.universityAutocomplete.getSelectedValues()
                if (selectedUniversities.length === 0) {
                    isValid = false
                    const universitySearch = document.getElementById("universitySearch")
                    if (universitySearch) {
                        universitySearch.classList.add("error")

                        // Create error message if it doesn't exist
                        const container = universitySearch.closest(".autocomplete-wrapper")
                        let errorMsg = container.querySelector(".error-message")
                        if (!errorMsg) {
                            errorMsg = document.createElement("div")
                            errorMsg.className = "error-message"
                            container.appendChild(errorMsg)
                        }
                        errorMsg.textContent = "Please select at least one university"
                    }
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

    console.log("Journey creation form initialization complete")
})
