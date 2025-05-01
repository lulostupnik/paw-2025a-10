/**
 * Password Strength Component
 *
 * Provides password strength visualization, validation, and confirmation matching
 */
let PasswordStrength = (() => {
    /**
     * Initialize password strength component
     * @param {Object} options Configuration options
     */

    var passwordField
    var confirmPasswordField
    var passwordMessage
    var passwordMatchMessage
    var messages
    var requirementElements = {}

    function init(options = {}) {
        // Default configuration
        const config = {
            passwordFieldId: "password",
            confirmFieldId: "confirmPassword",
            toggleBtnId: "togglePassword",
            confirmToggleBtnId: "toggleConfirmPassword",
            strengthBarId: "passwordStrengthBar",
            strengthLabelId: "passwordStrengthLabel",
            messageId: "passwordMessage",
            matchMessageId: "passwordMatchMessage",
            requirementIds: {
                length: "req-length",
                lowercase: "req-lowercase",
                uppercase: "req-uppercase",
                number: "req-number",
                special: "req-special",
            },
            ...options,
        }

        // Get DOM elements
        passwordField = document.getElementById(config.passwordFieldId)
        confirmPasswordField = document.getElementById(config.confirmFieldId)
        const togglePasswordBtn = document.getElementById(config.toggleBtnId)
        const toggleConfirmPasswordBtn = document.getElementById(config.confirmToggleBtnId)
        const passwordStrengthBar = document.getElementById(config.strengthBarId)
        const passwordStrengthLabel = document.getElementById(config.strengthLabelId)
        passwordMessage = document.getElementById(config.messageId)
        passwordMatchMessage = document.getElementById(config.matchMessageId)

        // Get requirement elements
        requirementElements = {}
        for (const [key, id] of Object.entries(config.requirementIds)) {
            requirementElements[key] = document.getElementById(id)
        }

        // Load internationalized messages
        messages = loadI18nMessages()

        // Validate required elements
        if (!passwordField) {
            console.error("Password field not found:", config.passwordFieldId)
            return
        }

        // Set up password visibility toggle
        if (togglePasswordBtn) {
            setupPasswordToggle(passwordField, togglePasswordBtn, "eyeIcon", "eyeSlashIcon")
        }

        // Set up confirm password visibility toggle
        if (toggleConfirmPasswordBtn && confirmPasswordField) {
            setupPasswordToggle(confirmPasswordField, toggleConfirmPasswordBtn, "confirmEyeIcon", "confirmEyeSlashIcon")
        }

        // Add event listeners for password strength
        passwordField.addEventListener("input", function () {
            updatePasswordStrengthUI(this.value)
            if (confirmPasswordField && confirmPasswordField.value) {
                checkPasswordsMatch()
            }
        })

        // Add event listener for password field focus
        passwordField.addEventListener("focus", function () {
            // Remove error highlighting when user starts typing again
            passwordField.classList.remove("password-error")

            // Update UI based on current value
            updatePasswordStrengthUI(this.value)
        })

        // Initialize on page load if password has a value
        if (passwordField.value) {
            updatePasswordStrengthUI(passwordField.value)
        }

        // Add event listeners for password confirmation
        if (confirmPasswordField) {
            confirmPasswordField.addEventListener("input", checkPasswordsMatch)

            // Initialize on page load if confirm password has a value
            if (confirmPasswordField.value) {
                checkPasswordsMatch()
            }
        }

        /**
         * Load internationalized messages from hidden input fields
         */
        function loadI18nMessages() {
            const messages = {
                veryWeak: getI18nValue("i18n-password-very-weak", "Very Weak"),
                weak: getI18nValue("i18n-password-weak", "Weak"),
                medium: getI18nValue("i18n-password-medium", "Medium"),
                strong: getI18nValue("i18n-password-strong", "Strong"),
                veryStrong: getI18nValue("i18n-password-very-strong", "Very Strong"),
                strengthLabel: getI18nValue("i18n-password-strength", "Password Strength"),
                addMoreStrength: getI18nValue("i18n-password-add-more", "Add more complexity"),
                goodPassword: getI18nValue("i18n-password-good", "Good password"),
                greatPassword: getI18nValue("i18n-password-great", "Great password"),
                passwordsMatch: getI18nValue("i18n-password-match", "Passwords match"),
                passwordsDontMatch: getI18nValue("i18n-password-mismatch", "Passwords do not match"),
                showPassword: getI18nValue("i18n-password-show", "Show password"),
                hidePassword: getI18nValue("i18n-password-hide", "Hide password"),
                requirements: {
                    length: getI18nValue("i18n-password-req-length", "8+ characters"),
                    lowercase: getI18nValue("i18n-password-req-lowercase", "Lowercase letter"),
                    uppercase: getI18nValue("i18n-password-req-uppercase", "Uppercase letter"),
                    number: getI18nValue("i18n-password-req-number", "Number"),
                    special: getI18nValue("i18n-password-req-special", "Special character"),
                },
            }

            return messages
        }

        /**
         * Get internationalized value from hidden input
         */
        function getI18nValue(id, defaultValue) {
            const element = document.getElementById(id)
            return element && element.value ? element.value : defaultValue
        }

        /**
         * Set up password visibility toggle
         */
        function setupPasswordToggle(field, toggleBtn, eyeIconId, eyeSlashIconId) {
            toggleBtn.addEventListener("click", function () {
                const eyeIcon = document.getElementById(eyeIconId)
                const eyeSlashIcon = document.getElementById(eyeSlashIconId)

                const type = field.getAttribute("type") === "password" ? "text" : "password"
                field.setAttribute("type", type)

                // Toggle eye icons
                if (eyeIcon && eyeSlashIcon) {
                    eyeIcon.style.display = type === "password" ? "inline" : "none"
                    eyeSlashIcon.style.display = type === "password" ? "none" : "inline"
                }

                // Update aria-label for accessibility
                this.setAttribute("aria-label", type === "password" ? messages.showPassword : messages.hidePassword)
            })
        }

        /**
         * Update password strength UI
         */
        function updatePasswordStrengthUI(password) {
            if (!passwordStrengthBar) return

            const { score, requirements } = evaluatePasswordStrength(password)

            // Update strength bar width
            passwordStrengthBar.style.width = score + "%"

            // Update UI based on score
            if (score === 0) {
                passwordStrengthBar.className = "password-bar"
                if (passwordStrengthLabel) passwordStrengthLabel.textContent = ""
                if (passwordMessage) passwordMessage.textContent = ""
            } else if (score < 25) {
                passwordStrengthBar.className = "password-bar strength-very-weak"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-very-weak"
                    passwordStrengthLabel.textContent = messages.veryWeak
                }
                if (passwordMessage) {
                    passwordMessage.textContent = messages.addMoreStrength
                    passwordMessage.classList.add("error-message")
                }
            } else if (score < 50) {
                passwordStrengthBar.className = "password-bar strength-weak"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-weak"
                    passwordStrengthLabel.textContent = messages.weak
                }
                if (passwordMessage) {
                    passwordMessage.textContent = messages.addMoreStrength
                    passwordMessage.classList.add("error-message")
                }
            } else if (score < 75) {
                passwordStrengthBar.className = "password-bar strength-medium"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-medium"
                    passwordStrengthLabel.textContent = messages.medium
                }
                if (passwordMessage) {
                    passwordMessage.textContent = messages.addMoreStrength
                    passwordMessage.classList.add("error-message")
                }
            } else if (score < 90) {
                passwordStrengthBar.className = "password-bar strength-strong"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-strong"
                    passwordStrengthLabel.textContent = messages.strong
                }
                if (passwordMessage) {
                    passwordMessage.textContent = messages.goodPassword
                    passwordMessage.classList.remove("error-message")
                }
            } else {
                passwordStrengthBar.className = "password-bar strength-very-strong"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-very-strong"
                    passwordStrengthLabel.textContent = messages.veryStrong
                }
                if (passwordMessage) {
                    passwordMessage.textContent = messages.goodPassword
                    passwordMessage.classList.remove("error-message")
                }
            }

            // Update requirement status
            for (const [key, element] of Object.entries(requirementElements)) {
                if (element) {
                    // Remove any previous error highlighting
                    element.classList.remove("highlight-error")
                    element.className = `requirement-item ${requirements[key] ? "requirement-met" : "requirement-unmet"}`
                }
            }
        }

        /**
         * Check if passwords match
         */
        function checkPasswordsMatch() {
            if (!confirmPasswordField || !passwordMatchMessage) return

            const password = passwordField.value
            const confirmPassword = confirmPasswordField.value

            if (confirmPassword) {
                if (password === confirmPassword) {
                    passwordMatchMessage.textContent = messages.passwordsMatch
                    passwordMatchMessage.className = "password-match-message match"
                    confirmPasswordField.classList.remove("error")
                } else {
                    passwordMatchMessage.textContent = messages.passwordsDontMatch
                    passwordMatchMessage.className = "password-match-message mismatch"
                    confirmPasswordField.classList.add("error")
                }
            } else {
                passwordMatchMessage.textContent = ""
                confirmPasswordField.classList.remove("error")
            }
        }
    }

    /**
     * Evaluate password strength
     */
    function evaluatePasswordStrength(password) {
        if (!password) return { score: 0, requirements: {} }

        const requirements = {
            length: password.length >= 8,
            lowercase: /[a-z]/.test(password),
            uppercase: /[A-Z]/.test(password),
            number: /[0-9]/.test(password),
            special: /[^A-Za-z0-9]/.test(password),
        }

        // Calculate strength score (0-100)
        let score = 0
        if (password.length > 0) {
            // Base score from requirements met (60%)
            const reqCount = Object.values(requirements).filter(Boolean).length
            score = (reqCount / 5) * 60

            // Additional score for length (40%)
            if (password.length >= 8) score += 10
            if (password.length >= 10) score += 10
            if (password.length >= 12) score += 10
            if (password.length >= 14) score += 10
        }

        return {
            score: Math.min(score, 100),
            requirements,
        }
    }

    /**
     * Highlight unmet requirements in red
     */
    function highlightUnmetRequirements() {
        if (!passwordField) return false

        const { requirements } = evaluatePasswordStrength(passwordField.value)
        let allMet = true

        // Add error class to password field
        passwordField.classList.add("password-error")

        // Highlight unmet requirements in red
        for (const [key, element] of Object.entries(requirementElements)) {
            if (element && !requirements[key]) {
                element.classList.add("highlight-error")
                allMet = false
            }
        }

        return allMet
    }

    /**
     * Validation
     */
    function isValid() {
        /*
       const password = passwordField.value
       const confirmPassword = confirmPasswordField.value

       // Check if passwords match
       if (password !== confirmPassword) {
           passwordMatchMessage.textContent = messages.passwordsDontMatch
           passwordMatchMessage.className = "password-match-message mismatch"
           confirmPasswordField.classList.add("error")
           return false
       }

       // Check password requirements

       const { score, requirements } = evaluatePasswordStrength(password)
       const allRequirementsMet =
           requirements.length &&
           requirements.lowercase &&
           requirements.number &&
           requirements.special &&
           requirements.uppercase

       if (!allRequirementsMet) {
           // Highlight unmet requirements
           highlightUnmetRequirements()
           return false
       }

       if (score < 75) {
           passwordMessage.textContent = messages.addMoreStrength
           passwordField.classList.add("error")
           highlightUnmetRequirements()
           return false
       }*/

        return true
    }

    // Public API
    return {
        init: init,
        isValid: isValid,
        highlightUnmetRequirements: highlightUnmetRequirements,
    }
})()

// Make available globally
window.PasswordStrength = PasswordStrength
