document.addEventListener("DOMContentLoaded", () => {
    const interestForm = document.getElementById("interestForm")
    const spanishNameInput = document.getElementById("spanishName")
    const englishNameInput = document.getElementById("englishName")

    // Focus on the first field when the page loads
    spanishNameInput.focus()

    // Optional: Auto-capitalize first letter of each word
    function capitalizeFirstLetter(input) {
        input.addEventListener("blur", function () {
            if (this.value) {
                this.value = this.value
                    .split(" ")
                    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                    .join(" ")
            }
        })
    }

    capitalizeFirstLetter(spanishNameInput)
    capitalizeFirstLetter(englishNameInput)

    // Optional: Form validation
    interestForm.addEventListener("submit", (event) => {
        let isValid = true

        // Validate Spanish name
        if (!spanishNameInput.value.trim()) {
            const errorElement = document.createElement("div")
            errorElement.className = "error-message"
            errorElement.textContent = "Spanish name is required"

            const existingError = spanishNameInput.parentNode.querySelector(".error-message")
            if (!existingError) {
                spanishNameInput.parentNode.appendChild(errorElement)
            }

            spanishNameInput.classList.add("error")
            isValid = false
        } else {
            spanishNameInput.classList.remove("error")
            const existingError = spanishNameInput.parentNode.querySelector(".error-message")
            if (existingError) {
                existingError.remove()
            }
        }

        // Validate English name
        if (!englishNameInput.value.trim()) {
            const errorElement = document.createElement("div")
            errorElement.className = "error-message"
            errorElement.textContent = "English name is required"

            const existingError = englishNameInput.parentNode.querySelector(".error-message")
            if (!existingError) {
                englishNameInput.parentNode.appendChild(errorElement)
            }

            englishNameInput.classList.add("error")
            isValid = false
        } else {
            englishNameInput.classList.remove("error")
            const existingError = englishNameInput.parentNode.querySelector(".error-message")
            if (existingError) {
                existingError.remove()
            }
        }

        if (!isValid) {
            event.preventDefault()
        }
    })
})
