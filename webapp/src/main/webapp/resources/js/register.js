/**
 * Registration Page Initialization
 *
 * Initializes and coordinates all components for the registration page
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing registration components...")

    // Import necessary modules (assuming they are available)
    // If not using modules, ensure these variables are declared globally or loaded via script tags
    // const PasswordStrength = window.PasswordStrength || {}
    const ListAutocomplete = window.ListAutocomplete || {}
    const FileUpload = window.FileUpload || {}
    const apiBaseUrl = window.apiBaseUrl;

    // Initialize password strength component
    try {
        window.passwordStrength = PasswordStrength.init({})
        console.log("Password strength component initialized")
    } catch (error) {
        console.error("Failed to initialize password strength component:", error)
    }

    // Initialize career autocomplete with API endpoint using ListAutocomplete in single-select mode
    try {
        const emptyMessage = document.getElementById("i18n-career-none")
            ? document.getElementById("i18n-career-none").value
            : "No career selected"

        window.careerAutocomplete = SingleOptionAutocomplete.init({
            selectId: "career",
            searchId: "careerSearch",
            dropdownId: "careerDropdown",
            selectedContainerId: "selectedCareer",
            apiEndpoint: `${apiBaseUrl}careers`,
            selectedValue: selectedCareer,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Set to single-select mode
            error: document.getElementById("career.errors") !== null,
        })

        console.log("Career autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize career autocomplete component:", error)
    }

    // Initialize university autocomplete with API endpoint using ListAutocomplete in single-select mode
    try {
        const emptyMessage = document.getElementById("i18n-university-none")
            ? document.getElementById("i18n-university-none").value
            : "No university selected"

        window.universityAutocomplete = SingleOptionAutocomplete.init({
            selectId: "originUniversity",
            searchId: "universitySearch",
            dropdownId: "universityDropdown",
            selectedContainerId: "selectedUniversity",
            apiEndpoint: `${apiBaseUrl}universities`,
            selectedValue: selectedUniversity,
            minChars: 1,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Set to single-select mode
            error: document.getElementById("originUniversity.errors") !== null,
        })

        console.log("University autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize university autocomplete component:", error)
    }

    try {
        const emptyMessage = document.getElementById("i18n-interests-none")
            ? document.getElementById("i18n-interests-none").value
            : "No interests selected"

        window.interestsAutocomplete = ListAutocomplete.init({
            selectId: "interestsSelect",
            searchId: "interestSearch",
            dropdownId: "interestDropdown",
            selectedContainerId: "selectedInterests",
            apiEndpoint: `${apiBaseUrl}interests`,
            selectedValue: selectedInterests,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: true, // Keep multi-select mode for interests
            error: document.getElementById("interests.errors") !== null,
        })
        console.log("Interests list autocomplete component initialized")
    } catch (error) {
        console.error("Failed to initialize interests list autocomplete component:", error)
    }

    // Initialize file upload
    try {
        window.fileUpload = FileUpload.init({
        })
        console.log("File upload component initialized")
    } catch (error) {
        console.error("Failed to initialize file upload component:", error)
    }

    console.log("Registration initialization complete")
})
