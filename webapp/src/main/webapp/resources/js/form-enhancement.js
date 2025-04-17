/**
 * Form enhancements for registration page
 * - Adds autocomplete for single-selection fields
 * - Adds password strength indicator with internationalization
 */
document.addEventListener("DOMContentLoaded", () => {
    // Initialize single-selection autocomplete for career field
    initSingleAutocomplete("career")

    // Initialize single-selection autocomplete for university field
    initSingleAutocomplete("originUniversity")

    // Initialize enhanced password strength meter with i18n support
    initPasswordStrength()
})

/**
 * Initialize single-selection autocomplete for a field
 * @param {string} fieldId - The ID of the select field
 */
function initSingleAutocomplete(fieldId) {
    const selectField = document.getElementById(fieldId)
    if (!selectField) return

    // Create wrapper for the select
    const wrapper = document.createElement("div")
    wrapper.className = "autocomplete-wrapper"
    selectField.parentNode.insertBefore(wrapper, selectField)

    // Create search input
    const searchInput = document.createElement("input")
    searchInput.type = "text"
    searchInput.className = "form-input"
    searchInput.placeholder = "Type to search..."
    wrapper.appendChild(searchInput)

    // Create dropdown container
    const dropdown = document.createElement("div")
    dropdown.className = "dropdown-menu"
    dropdown.style.display = "none"
    wrapper.appendChild(dropdown)

    // Get options from select field
    const options = Array.from(selectField.options).filter((option) => option.value)

    // Create dropdown items
    options.forEach((option) => {
        const item = document.createElement("div")
        item.className = "dropdown-item"
        item.textContent = option.textContent
        item.dataset.value = option.value

        // Handle item selection
        item.addEventListener("click", function () {
            searchInput.value = this.textContent
            selectField.value = this.dataset.value
            dropdown.style.display = "none"

            // Trigger change event on select
            const event = new Event("change")
            selectField.dispatchEvent(event)
        })

        dropdown.appendChild(item)
    })

    // Show dropdown on focus
    searchInput.addEventListener("focus", () => {
        dropdown.style.display = "block"
    })

    // Hide dropdown when clicking outside
    document.addEventListener("click", (e) => {
        if (!wrapper.contains(e.target)) {
            dropdown.style.display = "none"
        }
    })

    // Filter options as user types
    searchInput.addEventListener("input", function () {
        const value = this.value.toLowerCase()

        // Show dropdown when typing
        dropdown.style.display = "block"

        // Filter items
        Array.from(dropdown.children).forEach((item) => {
            const text = item.textContent.toLowerCase()
            item.style.display = text.includes(value) ? "block" : "none"
        })
    })

    // Initialize with selected value if any
    if (selectField.value) {
        const selectedOption = options.find((option) => option.value === selectField.value)
        if (selectedOption) {
            searchInput.value = selectedOption.textContent
        }
    }

    // Hide the original select
    selectField.style.display = "none"
}

/**
 * Initialize enhanced password strength meter with i18n support
 */
function initPasswordStrength() {
    const passwordField = document.getElementById("password")
    if (!passwordField) return

    // Get internationalized messages
    const messages = {
        veryWeak: document.getElementById("i18n-password-very-weak").value,
        weak: document.getElementById("i18n-password-weak").value,
        medium: document.getElementById("i18n-password-medium").value,
        strong: document.getElementById("i18n-password-strong").value,
        veryStrong: document.getElementById("i18n-password-very-strong").value,
        strengthLabel: document.getElementById("i18n-password-strength").value,
        addMoreStrength: document.getElementById("i18n-password-add-more").value,
        goodPassword: document.getElementById("i18n-password-good").value,
        greatPassword: document.getElementById("i18n-password-great").value,
        requirements: {
            length: document.getElementById("i18n-password-req-length").value,
            lowercase: document.getElementById("i18n-password-req-lowercase").value,
            uppercase: document.getElementById("i18n-password-req-uppercase").value,
            number: document.getElementById("i18n-password-req-number").value,
            special: document.getElementById("i18n-password-req-special").value,
        },
        emojis: {
            veryWeak: "😟",
            weak: "😐",
            medium: "🙂",
            strong: "😊",
            veryStrong: "👍",
        },
    }

    // Create password strength container
    const strengthContainer = document.createElement("div")
    strengthContainer.className = "password-strength"
    passwordField.parentNode.insertBefore(strengthContainer, passwordField.nextSibling)

    // Create meter
    const meter = document.createElement("div")
    meter.className = "password-meter"
    strengthContainer.appendChild(meter)

    // Create strength bar
    const bar = document.createElement("div")
    bar.className = "password-bar"
    meter.appendChild(bar)

    // Create feedback container
    const feedback = document.createElement("div")
    feedback.className = "password-feedback"
    strengthContainer.appendChild(feedback)

    // Create status container (left side)
    const status = document.createElement("div")
    status.className = "password-status"
    feedback.appendChild(status)

    // Create emoji span
    const emoji = document.createElement("span")
    emoji.className = "password-emoji"
    status.appendChild(emoji)

    // Create strength label
    const strengthLabel = document.createElement("span")
    strengthLabel.className = "password-label"
    status.appendChild(strengthLabel)

    // Create message (right side)
    const message = document.createElement("span")
    message.className = "password-message"
    feedback.appendChild(message)

    // Create requirements container
    const requirementsContainer = document.createElement("div")
    requirementsContainer.className = "password-requirements"
    strengthContainer.appendChild(requirementsContainer)

    // Define requirements
    const requirements = [
        { id: "length", regex: /.{8,}/, text: messages.requirements.length },
        { id: "lowercase", regex: /[a-z]/, text: messages.requirements.lowercase },
        { id: "uppercase", regex: /[A-Z]/, text: messages.requirements.uppercase },
        { id: "number", regex: /[0-9]/, text: messages.requirements.number },
        { id: "special", regex: /[^A-Za-z0-9]/, text: messages.requirements.special },
    ]

    // Create requirement elements
    requirements.forEach((req) => {
        const reqElement = document.createElement("div")
        reqElement.id = `req-${req.id}`
        reqElement.className = "requirement-item requirement-unmet"

        // Create icon
        const icon = document.createElementNS("http://www.w3.org/2000/svg", "svg")
        icon.setAttribute("class", "requirement-icon")
        icon.setAttribute("viewBox", "0 0 20 20")
        icon.setAttribute("fill", "currentColor")
        icon.setAttribute("width", "16")
        icon.setAttribute("height", "16")

        const path = document.createElementNS("http://www.w3.org/2000/svg", "path")
        path.setAttribute("fill-rule", "evenodd")
        path.setAttribute(
            "d",
            "M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z",
        )
        path.setAttribute("clip-rule", "evenodd")

        icon.appendChild(path)
        reqElement.appendChild(icon)

        // Add text
        const text = document.createElement("span")
        text.textContent = req.text
        reqElement.appendChild(text)

        requirementsContainer.appendChild(reqElement)
    })

    // Update strength on input
    passwordField.addEventListener("input", function () {
        const password = this.value
        const { score, meetsAllRequirements } = evaluatePasswordStrength(password, requirements)
        updatePasswordStrengthUI(bar, strengthLabel, emoji, message, requirements, score, meetsAllRequirements, messages)
    })
}

/**
 * Evaluate password strength with detailed requirements
 * @param {string} password - The password to evaluate
 * @param {Array} requirements - The list of requirements
 * @returns {Object} - Score and requirements status
 */
function evaluatePasswordStrength(password, requirements) {
    if (!password) return { score: 0, meetsAllRequirements: false }

    let score = 0
    let reqsMet = 0

    // Check each requirement
    requirements.forEach((req) => {
        if (req.regex.test(password)) {
            reqsMet++
        }
    })

    // Base score from requirements met
    score = (reqsMet / requirements.length) * 60

    // Additional score for length
    if (password.length >= 8) score += 10
    if (password.length >= 10) score += 10
    if (password.length >= 12) score += 10
    if (password.length >= 14) score += 10

    // Check if all requirements are met
    const meetsAllRequirements = reqsMet === requirements.length

    return {
        score: Math.min(score, 100),
        meetsAllRequirements,
    }
}

/**
 * Update password strength UI with internationalized messages
 * @param {HTMLElement} bar - The strength bar element
 * @param {HTMLElement} label - The strength label element
 * @param {HTMLElement} emoji - The emoji element
 * @param {HTMLElement} message - The message element
 * @param {Array} requirements - The list of requirements
 * @param {number} score - The strength score (0-100)
 * @param {boolean} meetsAllRequirements - Whether all requirements are met
 * @param {Object} messages - The internationalized messages
 */
function updatePasswordStrengthUI(bar, label, emoji, message, requirements, score, meetsAllRequirements, messages) {
    // Update bar width
    bar.style.width = score + "%"

    // Update UI based on score
    if (score === 0) {
        bar.className = "password-bar"
        label.className = "password-label"
        label.textContent = ""
        emoji.textContent = ""
        message.textContent = ""
    } else if (score < 25) {
        bar.className = "password-bar strength-very-weak"
        label.className = "password-label text-very-weak"
        label.textContent = messages.veryWeak + "!"
        emoji.textContent = messages.emojis.veryWeak
        message.textContent = messages.addMoreStrength
    } else if (score < 50) {
        bar.className = "password-bar strength-weak"
        label.className = "password-label text-weak"
        label.textContent = messages.weak + "!"
        emoji.textContent = messages.emojis.weak
        message.textContent = messages.addMoreStrength
    } else if (score < 75) {
        bar.className = "password-bar strength-medium"
        label.className = "password-label text-medium"
        label.textContent = messages.medium
        emoji.textContent = messages.emojis.medium
        message.textContent = messages.goodPassword
    } else if (score < 90) {
        bar.className = "password-bar strength-strong"
        label.className = "password-label text-strong"
        label.textContent = messages.strong
        emoji.textContent = messages.emojis.strong
        message.textContent = messages.goodPassword
    } else {
        bar.className = "password-bar strength-very-strong"
        label.className = "password-label text-very-strong"
        label.textContent = messages.veryStrong
        emoji.textContent = messages.emojis.veryStrong
        message.textContent = messages.greatPassword
    }

    // Update requirement status
    const password = document.getElementById("password").value
    requirements.forEach((req) => {
        const reqElement = document.getElementById(`req-${req.id}`)
        if (req.regex.test(password)) {
            reqElement.className = "requirement-item requirement-met"
        } else {
            reqElement.className = "requirement-item requirement-unmet"
        }
    })
}
