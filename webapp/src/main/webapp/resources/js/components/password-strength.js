
let PasswordStrength = (() => {
    

    let passwordField
    let confirmPasswordField
    let passwordMatchMessage
    let messages

    function init(options = {}) {
        // Default configuration
        const config = {
            passwordFieldId: "password",
            confirmFieldId: "confirmPassword",
            toggleBtnId: "togglePassword",
            confirmToggleBtnId: "toggleConfirmPassword",
            strengthBarId: "passwordStrengthBar",
            strengthLabelId: "passwordStrengthLabel",
            matchMessageId: "passwordMatchMessage",
            ...options,
        }

        // Get DOM elements
        passwordField = document.getElementById(config.passwordFieldId)
        confirmPasswordField = document.getElementById(config.confirmFieldId)
        const togglePasswordBtn = document.getElementById(config.toggleBtnId)
        const toggleConfirmPasswordBtn = document.getElementById(config.confirmToggleBtnId)
        const passwordStrengthBar = document.getElementById(config.strengthBarId)
        const passwordStrengthLabel = document.getElementById(config.strengthLabelId)
        passwordMatchMessage = document.getElementById(config.matchMessageId)

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

        
        function loadI18nMessages() {
            const messages = {
                veryWeak: getI18nValue("i18n-password-very-weak", "Very Weak"),
                weak: getI18nValue("i18n-password-weak", "Weak"),
                medium: getI18nValue("i18n-password-medium", "Medium"),
                strong: getI18nValue("i18n-password-strong", "Strong"),
                veryStrong: getI18nValue("i18n-password-very-strong", "Very Strong"),
                strengthLabel: getI18nValue("i18n-password-strength", "Password Strength"),
                passwordsMatch: getI18nValue("i18n-password-match", "Passwords match"),
                passwordsDontMatch: getI18nValue("i18n-password-mismatch", "Passwords do not match"),
                showPassword: getI18nValue("i18n-password-show", "Show password"),
                hidePassword: getI18nValue("i18n-password-hide", "Hide password"),
            }

            return messages
        }

        
        function getI18nValue(id, defaultValue) {
            const element = document.getElementById(id)
            return element && element.value ? element.value : defaultValue
        }

        
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

        
        function updatePasswordStrengthUI(password) {
            if (!passwordStrengthBar) return

            const score = evaluatePasswordStrength(password)

            // Update strength bar width
            passwordStrengthBar.style.width = score + "%"

            // Update UI based on score
            if (score === 0) {
                passwordStrengthBar.className = "password-bar"
                if (passwordStrengthLabel) passwordStrengthLabel.textContent = ""
            } else if (score < 25) {
                passwordStrengthBar.className = "password-bar strength-very-weak"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-very-weak"
                    passwordStrengthLabel.textContent = messages.veryWeak
                }
            } else if (score < 50) {
                passwordStrengthBar.className = "password-bar strength-weak"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-weak"
                    passwordStrengthLabel.textContent = messages.weak
                }
            } else if (score < 75) {
                passwordStrengthBar.className = "password-bar strength-medium"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-medium"
                    passwordStrengthLabel.textContent = messages.medium
                }
            } else if (score < 90) {
                passwordStrengthBar.className = "password-bar strength-strong"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-strong"
                    passwordStrengthLabel.textContent = messages.strong
                }
            } else {
                passwordStrengthBar.className = "password-bar strength-very-strong"
                if (passwordStrengthLabel) {
                    passwordStrengthLabel.className = "password-label text-very-strong"
                    passwordStrengthLabel.textContent = messages.veryStrong
                }
            }
        }

        
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

    
    function evaluatePasswordStrength(password) {
        if (!password) return 0

        // Calculate a simple strength score based on length and character variety
        let score = 0

        // Base score from length (up to 60 points)
        if (password.length > 0) {
            score += Math.min(password.length * 5, 60)
        }

        // Additional points for character variety
        if (/[a-z]/.test(password)) score += 10; // lowercase
        if (/[A-Z]/.test(password)) score += 10; // uppercase
        if (/[0-9]/.test(password)) score += 10; // numbers
        if (/[^A-Za-z0-9]/.test(password)) score += 10; // special chars

        return Math.min(score, 100);
    }


    // Public API
    return {
        init: init,
    }
})()

// Make available globally
window.PasswordStrength = PasswordStrength
