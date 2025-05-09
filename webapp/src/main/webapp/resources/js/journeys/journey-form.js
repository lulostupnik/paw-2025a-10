/**
 * Journey Creation Form Initialization
 *
 * Initializes and coordinates all components for the journey creation form
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing journey creation form components...")

    // Import necessary modules
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

        window.universityAutocomplete = window.SingleOptionAutocomplete.init({
            selectId: "destinationUniversity",
            searchId: "universitySearch",
            dropdownId: "universityDropdown",
            selectedContainerId: "selectedUniversities",
            apiEndpoint: `${apiBaseUrl}universities`,
            selectedValue: journeySelectedUniversity,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Single-select mode

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

        const form = document.getElementById("journeyForm");
        if (form) {
            form.addEventListener("submit", (e) => {
                e.preventDefault();
                form.submit();
            });
        }


        console.log("Journey creation form initialization complete");
    });