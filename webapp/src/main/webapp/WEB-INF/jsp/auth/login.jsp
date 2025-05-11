<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="login.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<style>
    .popup-overlay {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 1000;
        justify-content: center;
        align-items: center;
    }

    .popup-container {
        background-color: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        max-width: 400px;
        width: 90%;
        text-align: center;
    }

    .popup-icon {
        color: #4CAF50;
        width: 48px;
        height: 48px;
        margin: 0 auto 16px;
    }

    .popup-title {
        font-size: 1.25rem;
        font-weight: 600;
        margin-bottom: 8px;
    }

    .popup-message {
        margin-bottom: 16px;
        color: #666;
    }

    .popup-button {
        background-color: #4361ee;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        font-weight: 500;
    }

    .popup-button:hover {
        background-color: #3a56d4;
    }
</style>
<body>

<jsp:include page="../components/navbar.jsp" />


<c:if test="${registrationSuccess eq true}">
    <div id="successPopup" class="popup-overlay" style="display: flex;">
        <div class="popup-container">
            <svg xmlns="http://www.w3.org/2000/svg" class="popup-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 class="popup-title"><spring:message code="register.success.title" text="Registration Successful" /></h3>
            <p class="popup-message">
                <spring:message code="register.success.message" text="A verification email has been sent to your email address. Please check your inbox and follow the instructions to validate your account." />
            </p>
            <button type="button" class="popup-button" id="closePopup"><spring:message code="register.success.button" text="Got it" /></button>
        </div>
    </div>
</c:if>


<c:if test="${resetPassword eq true}">
    <div id="resetPasswordPopup" class="popup-overlay" style="display: flex;">
        <div class="popup-container">
            <svg xmlns="http://www.w3.org/2000/svg" class="popup-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 class="popup-title"><spring:message code="reset.password.success.title" text="Password Reset Successful" /></h3>
            <p class="popup-message">
                <spring:message code="reset.password.success.message" text="Your password has been reset successfully. You can now log in with your new password." />
            </p>
            <button type="button" class="popup-button" id="closeResetPopup"><spring:message code="reset.password.success.button" text="Got it" /></button>
        </div>
    </div>
</c:if>


<c:if test="${emailSuccess eq true}">
    <div id="emailSuccessPopup" class="popup-overlay" style="display: flex;">
        <div class="popup-container">
            <svg xmlns="http://www.w3.org/2000/svg" class="popup-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <h3 class="popup-title"><spring:message code="email.password.reset.title" text="Email Sent" /></h3>
            <p class="popup-message">
                <spring:message code="email.password.reset.message" text="An email with instructions to reset your password has been sent to your email address. Please check your inbox and follow the link to reset your password." />
            </p>
            <button type="button" class="popup-button" id="closeEmailSuccessPopup"><spring:message code="email.password.reset.button" text="Got it" /></button>
        </div>
    </div>
</c:if>


<div id="emailValidationPopup" class="popup-overlay">
    <div class="popup-container">
        <svg xmlns="http://www.w3.org/2000/svg" class="popup-icon" style="color: #f59e0b;" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
        </svg>
        <h3 class="popup-title"><spring:message code="email.validation.title" text="Invalid Email" /></h3>
        <p class="popup-message" id="emailValidationMessage">
            <spring:message code="email.validation.message" text="Please enter a valid email address." />
        </p>
        <button type="button" class="popup-button" id="closeEmailValidationPopup"><spring:message code="email.validation.button" text="OK" /></button>
    </div>
</div>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">

                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="login.title"/></h1>
            <p class="auth-subtitle"><spring:message code="login.subtitle" text="Sign in to your account to continue"/></p>
        </div>

        <c:if test="${param.error != null}">
            <div class="alert alert-error">
                <svg xmlns="http://www.w3.org/2000/svg" class="alert-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <div class="alert-content">
                    <h3 class="alert-title"><spring:message code="login.error.title" text="Authentication Failed"/></h3>
                    <p class="alert-message"><spring:message code="login.error.description" text="Invalid username or password"/></p>
                </div>
            </div>
        </c:if>

        <c:url value="/login" var="loginUrl" />
        <form:form action="${loginUrl}" method="post" enctype="application/x-www-form-urlencoded" class="auth-form">
            <div class="form-group">
                <label for="j_username" class="form-label required-field">
                    <spring:message code="login.email" text="Email"/>
                </label>
                <input id="j_username"
                       name="j_username"
                       type="email"
                       placeholder="<spring:message code="login.email.placeholder" text="Enter your email"/>"
                       class="form-input"
                />
            </div>

            <div class="form-group">
                <label for="j_password" class="form-label required-field">
                    <spring:message code="login.password" text="Password"/>
                </label>
                <div class="password-field-container">
                    <input id="j_password"
                           name="j_password"
                           type="password"
                           placeholder="<spring:message code="login.password.placeholder" text="Enter your password"/>"
                           class="form-input"
                    />
                    <button type="button" id="togglePassword" class="password-toggle-button" aria-label="<spring:message code="password.show" text="Show password"/>">
                        <svg id="eyeIcon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                        <svg id="eyeSlashIcon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" style="display: none;">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                    </button>
                </div>
            </div>



            <div class="login-group">
                <div class="checkbox-container">
                    <label class="checkbox-wrapper">
                        <input name="j_rememberme" type="checkbox" class="checkbox-input" />
                        <span class="checkbox-mark"></span>
                    </label>
                    <label class="checkbox-label" for="j_rememberme">
                        <spring:message code="remember_me" text="Remember me"/>
                    </label>
                </div>
                <a href="<c:url value='/forgot_pass'/>" class="forgot-password">
                    <spring:message code="login.forgot_password" text="Forgot password?"/>
                </a>
            </div>

            <button type="submit" class="form-button">
                <spring:message code="login.submit" text="Sign In"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <spring:message code="login.no.account" text="Don't have an account?"/>
            <a href="<c:url value='/register'/>" class="auth-link">
                <spring:message code="login.register" text="Sign up"/>
            </a>
        </div>
    </div>
</div>

<script>
    // Simple form validation
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('.auth-form');
        const emailInput = document.getElementById('j_username');
        const passwordInput = document.getElementById('j_password');
        const closePopupButton = document.getElementById('closePopup');
        const closeResetPopupButton = document.getElementById('closeResetPopup');
        const closeEmailSuccessPopupButton = document.getElementById('closeEmailSuccessPopup');
        const closeEmailValidationPopupButton = document.getElementById('closeEmailValidationPopup');
        const emailValidationPopup = document.getElementById('emailValidationPopup');
        const emailValidationMessage = document.getElementById('emailValidationMessage');

        // Close registration success popup
        if (closePopupButton) {
            closePopupButton.addEventListener('click', function() {
                document.getElementById('successPopup').style.display = 'none';
            });
        }

        // Close password reset popup
        if (closeResetPopupButton) {
            closeResetPopupButton.addEventListener('click', function() {
                document.getElementById('resetPasswordPopup').style.display = 'none';
            });
        }

        // Close email success popup
        if (closeEmailSuccessPopupButton) {
            closeEmailSuccessPopupButton.addEventListener('click', function() {
                document.getElementById('emailSuccessPopup').style.display = 'none';
            });
        }

        // Close email validation popup
        if (closeEmailValidationPopupButton) {
            closeEmailValidationPopupButton.addEventListener('click', function() {
                emailValidationPopup.style.display = 'none';
            });
        }

        form.addEventListener('submit', function(e) {
            let isValid = true;

            // Validate email
            if (!emailInput.value.trim()) {
                showEmailValidationPopup('<spring:message code="login.email.required" text="Email is required"/>');
                isValid = false;
            } else if (!isValidEmail(emailInput.value.trim())) {
                showEmailValidationPopup('<spring:message code="login.email.invalid" text="Please enter a valid email address"/>');
                isValid = false;
            } else {
                clearError(emailInput);
            }

            // Validate password
            if (!passwordInput.value.trim()) {
                showError(passwordInput, '<spring:message code="login.password.required" text="Password is required"/>');
                isValid = false;
            } else {
                clearError(passwordInput);
            }

            if (!isValid) {
                e.preventDefault();
            }
        });

        // Input event listeners for real-time validation
        emailInput.addEventListener('input', function() {
            if (this.value.trim() && isValidEmail(this.value.trim())) {
                clearError(this);
            }
        });

        passwordInput.addEventListener('input', function() {
            if (this.value.trim()) {
                clearError(this);
            }
        });

        function showEmailValidationPopup(message) {
            emailInput.classList.add('error');
            emailValidationMessage.textContent = message;
            emailValidationPopup.style.display = 'flex';
        }

        function showError(input, message) {
            const formGroup = input.closest('.form-group');
            let errorElement = formGroup.querySelector('.error-message');

            input.classList.add('error');

            if (!errorElement) {
                errorElement = document.createElement('div');
                errorElement.className = 'error-message';
                errorElement.textContent = message;
                formGroup.appendChild(errorElement);
            } else {
                errorElement.textContent = message;
            }
        }

        function clearError(input) {
            const formGroup = input.closest('.form-group');
            const errorElement = formGroup.querySelector('.error-message');

            input.classList.remove('error');

            if (errorElement) {
                errorElement.remove();
            }
        }

        function isValidEmail(email) {
            const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }
    });

    // Password toggle functionality
    document.addEventListener('DOMContentLoaded', function() {
        const passwordField = document.getElementById('j_password');
        const togglePasswordBtn = document.getElementById('togglePassword');
        const eyeIcon = document.getElementById('eyeIcon');
        const eyeSlashIcon = document.getElementById('eyeSlashIcon');


        if (togglePasswordBtn && passwordField) {
            togglePasswordBtn.addEventListener('click', function() {
                const type = passwordField.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordField.setAttribute('type', type);

                // Toggle eye icons
                if (eyeIcon && eyeSlashIcon) {
                    eyeIcon.style.display = type === 'password' ? 'inline' : 'none';
                    eyeSlashIcon.style.display = type === 'password' ? 'none' : 'inline';
                }

                // Update aria-label for accessibility
                this.setAttribute('aria-label', type === 'password' ?
                    '<spring:message code="password.show" text="Show password"/>' :
                    '<spring:message code="password.hide" text="Hide password"/>');
            });
        }
    });
</script>
</body>
</html>