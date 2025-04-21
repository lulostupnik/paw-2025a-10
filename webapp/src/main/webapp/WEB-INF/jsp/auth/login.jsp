<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title><spring:message code="login.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<!-- Navigation -->
<jsp:include page="../components/navbar.jsp" />
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <!-- You can add your logo here -->
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

            <div class="checkbox-container">
                <label class="checkbox-label">
                    <input name="j_rememberme"
                           type="checkbox"
                           class="checkbox-input" />
                    <span class="checkbox-custom"></span>
                    <span><spring:message code="remember_me" text="Remember me"/></span>
                </label>
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

        form.addEventListener('submit', function(e) {
            let isValid = true;

            // Validate email
            if (!emailInput.value.trim()) {
                showError(emailInput, '<spring:message code="login.email.required" text="Email is required"/>');
                isValid = false;
            } else if (!isValidEmail(emailInput.value.trim())) {
                showError(emailInput, '<spring:message code="login.email.invalid" text="Please enter a valid email address"/>');
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
                    'Show password' : 'Hide password');
            });
        }
    });
</script>
</body>
</html>
