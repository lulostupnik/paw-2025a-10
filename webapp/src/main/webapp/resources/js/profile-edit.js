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
    // Handle file input change
    if (fileInput) {
        fileInput.addEventListener("change", (e) => {
            const file = e.target.files[0]
            if (file) {
                // Validate file type
                if (!file.type.startsWith("image/")) {
                    showError("Please select a valid image file.")
                    return
                }

                // Validate file size (5MB limit)
                if (file.size > 5 * 1024 * 1024) {
                    showError("File size must be less than 5MB.")
                    return
                }

                // Preview the image
                const reader = new FileReader()
                reader.onload = (e) => {
                    // Remove existing content
                    currentAvatar.innerHTML = ""

                    // Create new image element
                    const img = document.createElement("img")
                    img.src = e.target.result
                    img.className = "avatar-image"
                    img.alt = "Profile preview"

                    currentAvatar.appendChild(img)
                }
                reader.readAsDataURL(file)
            }
        })
    }


    // Handle avatar container click
    if (currentAvatar) {
        currentAvatar.addEventListener("click", () => {
            fileInput.click()
        })
    }

    // Form validation
    if (form) {
        form.addEventListener("submit", (e) => {
            if (!validateForm()) {
                e.preventDefault()
                return false
            }

            // Show loading state
            submitButton.classList.add("loading")
            submitButton.disabled = true
        })
    }

    // Real-time validation
    const inputs = form.querySelectorAll(".form-input.required")
    inputs.forEach((input) => {
        input.addEventListener("blur", function () {
            validateField(this)
        })

        input.addEventListener("input", function () {
            // Remove error state on input
            if (this.classList.contains("error")) {
                this.classList.remove("error")
                const errorMsg = this.parentNode.querySelector(".error-message")
                if (errorMsg) {
                    errorMsg.style.display = "none"
                }
            }
        })
    })

    function validateForm() {
        let isValid = true
        const requiredInputs = form.querySelectorAll(".form-input.required")

        requiredInputs.forEach((input) => {
            if (!validateField(input)) {
                isValid = false
            }
        })

        return isValid
    }

    function validateField(field) {
        const value = field.value.trim()
        let isValid = true
        let errorMessage = ""

        // Required field validation
        if (field.classList.contains("required") && !value) {
            isValid = false
            errorMessage = "This field is required."
        }

        // Email validation
        if (field.type === "email" && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            if (!emailRegex.test(value)) {
                isValid = false
                errorMessage = "Please enter a valid email address."
            }
        }

        // Username validation
        if (field.id === "username" && value) {
            if (value.length < 2 || value.length > 50) {
                isValid = false
                errorMessage = "Username must be between 2 and 50 characters."
            }
        }

        // Name validation
        if ((field.id === "firstName" || field.id === "lastName") && value) {
            if (value.length < 2 || value.length > 100) {
                isValid = false
                errorMessage = "Name must be between 2 and 100 characters."
            }
        }

        // Update field state
        if (isValid) {
            field.classList.remove("error")
            field.classList.add("success")
        } else {
            field.classList.remove("success")
            field.classList.add("error")
            showFieldError(field, errorMessage)
        }

        return isValid
    }

    function showFieldError(field, message) {
        let errorElement = field.parentNode.querySelector(".error-message")
        if (!errorElement) {
            errorElement = document.createElement("div")
            errorElement.className = "error-message"
            field.parentNode.appendChild(errorElement)
        }
        errorElement.textContent = message
        errorElement.style.display = "block"
    }

    function showError(message) {
        // You can implement a toast notification or alert here
        alert(message)
    }
})
