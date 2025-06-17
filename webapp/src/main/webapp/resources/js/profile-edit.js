document.addEventListener("DOMContentLoaded", () => {
    const fileInput = document.getElementById("profilePicture")
    const avatarPreview = document.getElementById("avatarPreview")
    const avatarPlaceholder = document.getElementById("avatarPlaceholder")
    const currentAvatar = document.getElementById("currentAvatar")
    const form = document.querySelector(".profile-edit-form")
    const submitButton = form.querySelector(".form-button")

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


    // Handle avatar container click
    if (currentAvatar) {
        currentAvatar.addEventListener("click", () => {
            fileInput.click()
        })
    }
})
