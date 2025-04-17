<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="register.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="auth-container">
    <a href="<c:url value="/"/>" class="back-link">
        <svg xmlns="http://www.w3.org/2000/svg" class="back-link-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        <spring:message code="register.back"/>
    </a>

    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <!-- You can add your logo here -->
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="register.title"/></h1>
            <p class="auth-subtitle"><spring:message code="register.subtitle" text="Create your account to get started"/></p>
        </div>

        <!-- Hidden internationalization messages for JavaScript -->
        <input type="hidden" id="i18n-password-very-weak" value="<spring:message code="password.strength.very-weak" text="Very Weak"/>" />
        <input type="hidden" id="i18n-password-weak" value="<spring:message code="password.strength.weak" text="Weak"/>" />
        <input type="hidden" id="i18n-password-medium" value="<spring:message code="password.strength.medium" text="Medium"/>" />
        <input type="hidden" id="i18n-password-strong" value="<spring:message code="password.strength.strong" text="Strong"/>" />
        <input type="hidden" id="i18n-password-very-strong" value="<spring:message code="password.strength.very-strong" text="Very Strong"/>" />
        <input type="hidden" id="i18n-password-strength" value="<spring:message code="password.strength.label" text="Password Strength"/>" />
        <input type="hidden" id="i18n-password-add-more" value="<spring:message code="password.strength.add-more" text="Add more complexity"/>" />
        <input type="hidden" id="i18n-password-good" value="<spring:message code="password.strength.good" text="Good password"/>" />
        <input type="hidden" id="i18n-password-great" value="<spring:message code="password.strength.great" text="Great password"/>" />
        <input type="hidden" id="i18n-password-req-length" value="<spring:message code="password.req.length" text="8+ characters"/>" />
        <input type="hidden" id="i18n-password-req-lowercase" value="<spring:message code="password.req.lowercase" text="Lowercase letter"/>" />
        <input type="hidden" id="i18n-password-req-uppercase" value="<spring:message code="password.req.uppercase" text="Uppercase letter"/>" />
        <input type="hidden" id="i18n-password-req-number" value="<spring:message code="password.req.number" text="Number"/>" />
        <input type="hidden" id="i18n-password-req-special" value="<spring:message code="password.req.special" text="Special character"/>" />
        <input type="hidden" id="i18n-password-match" value="<spring:message code="password.match" text="Passwords match"/>" />
        <input type="hidden" id="i18n-password-mismatch" value="<spring:message code="password.mismatch" text="Passwords do not match"/>" />
        <input type="hidden" id="i18n-password-show" value="<spring:message code="password.show" text="Show password"/>" />
        <input type="hidden" id="i18n-password-hide" value="<spring:message code="password.hide" text="Hide password"/>" />

        <c:url var="registerUrl" value="/register"/>
        <form:form modelAttribute="createUserForm" action="${registerUrl}" method="post" enctype="multipart/form-data" class="auth-form">
            <div class="auth-columns">
                <div class="auth-column">
                    <!-- Personal Information -->
                    <div class="form-group">
                        <form:label path="email" cssClass="form-label required-field">
                            <spring:message code="createJourney.userEmail"/>
                        </form:label>
                        <form:input path="email" type="email" cssClass="form-input ${not empty errors.getFieldError('email') ? 'error' : ''}"
                                    placeholder="example@email.com" required="true" />
                        <form:errors path="email" cssClass="error-message" />
                    </div>

                    <!-- Enhanced Password Field with Strength Meter -->
                    <div class="form-group">
                        <form:label path="password" cssClass="form-label required-field">
                            <spring:message code="createJourney.password"/>
                        </form:label>
                        <div class="password-field-container">
                            <form:password path="password" id="password"
                                           cssClass="form-input ${not empty errors.getFieldError('password') ? 'error' : ''}"
                                           placeholder="••••••••" required="true" />
                            <button type="button" id="togglePassword" class="password-toggle-button" aria-label="Show password">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="eyeIcon">
                                    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                </svg>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="eyeSlashIcon" style="display: none;">
                                    <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                    <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                    <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                </svg>
                            </button>
                        </div>
                        <form:errors path="password" cssClass="error-message" />

                        <!-- Single password strength meter -->
                        <div class="password-strength">
                            <div class="password-meter">
                                <div class="password-bar" id="passwordStrengthBar"></div>
                            </div>
                            <div class="password-feedback">
                                <div class="password-status">
                                    <span id="passwordStrengthLabel"></span>
                                </div>
                                <span class="password-message" id="passwordMessage"></span>
                            </div>

                            <!-- Password requirements -->
                            <div class="password-requirements">
                                <div class="requirement-item" id="req-length">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.length" text="8+ characters"/></span>
                                </div>
                                <div class="requirement-item" id="req-lowercase">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.lowercase" text="Lowercase letter"/></span>
                                </div>
                                <div class="requirement-item" id="req-uppercase">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.uppercase" text="Uppercase letter"/></span>
                                </div>
                                <div class="requirement-item" id="req-number">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.number" text="Number"/></span>
                                </div>
                                <div class="requirement-item" id="req-special">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.special" text="Special character"/></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Password Confirmation Field -->
                    <div class="form-group">
                        <label for="confirmPassword" class="form-label required-field">
                            <spring:message code="register.confirmPassword" text="Confirm Password"/>
                        </label>
                        <div class="password-field-container">
                            <input type="password" id="confirmPassword" name="confirmPassword"
                                   class="form-input" placeholder="••••••••" required />
                            <button type="button" id="toggleConfirmPassword" class="password-toggle-button" aria-label="Show password">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="confirmEyeIcon">
                                    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                </svg>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="confirmEyeSlashIcon" style="display: none;">
                                    <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                    <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                    <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                </svg>
                            </button>
                        </div>
                        <div id="passwordMatchMessage" class="password-match-message"></div>
                    </div>

                    <div class="form-group">
                        <form:label path="firstName" cssClass="form-label required-field">
                            <spring:message code="createJourney.firstName"/>
                        </form:label>
                        <form:input path="firstName" cssClass="form-input ${not empty errors.getFieldError('firstName') ? 'error' : ''}"
                                    placeholder="John" required="true" />
                        <form:errors path="firstName" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="lastName" cssClass="form-label required-field">
                            <spring:message code="createJourney.lastName"/>
                        </form:label>
                        <form:input path="lastName" cssClass="form-input ${not empty errors.getFieldError('lastName') ? 'error' : ''}"
                                    placeholder="Doe" required="true" />
                        <form:errors path="lastName" cssClass="error-message" />
                    </div>
                </div>

                <div class="auth-column">
                    <div class="form-group">
                        <form:label path="username" cssClass="form-label required-field">
                            <spring:message code="createJourney.username"/>
                        </form:label>
                        <form:input path="username" cssClass="form-input ${not empty errors.getFieldError('username') ? 'error' : ''}"
                                    placeholder="johndoe" required="true" />
                        <form:errors path="username" cssClass="error-message" />
                    </div>

                    <!-- Career field with enhanced autocomplete -->
                    <div class="form-group">
                        <form:label path="career" cssClass="form-label required-field">
                            <spring:message code="event.career"/>
                        </form:label>
                        <div class="autocomplete-wrapper">
                            <form:select path="career" id="career" cssClass="form-select ${not empty errors.getFieldError('career') ? 'error' : ''}" required="true" style="display: none;">
                                <form:option value=""><spring:message code="event.career.select"/></form:option>
                                <c:forEach var="item" items="${careers}">
                                    <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                                </c:forEach>
                            </form:select>
                            <input type="text" id="careerSearch" class="form-input" placeholder="<spring:message code="event.career.search" text="Type to search..."/>" />
                            <div id="careerDropdown" class="dropdown-menu" style="display: none;">
                                <c:forEach var="item" items="${careers}">
                                    <div class="dropdown-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <form:errors path="career" cssClass="error-message" />
                    </div>

                    <!-- Origin University field with enhanced autocomplete -->
                    <div class="form-group">
                        <form:label path="originUniversity" cssClass="form-label required-field">
                            <spring:message code="createJourney.originUniversity"/>
                        </form:label>
                        <div class="autocomplete-wrapper">
                            <form:select path="originUniversity" id="originUniversity" cssClass="form-select ${not empty errors.getFieldError('originUniversity') ? 'error' : ''}" required="true" style="display: none;">
                                <form:option value=""><spring:message code="createJourney.originUniversity.select"/></form:option>
                                <c:forEach var="item" items="${universities}">
                                    <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                                </c:forEach>
                            </form:select>
                            <input type="text" id="universitySearch" class="form-input" placeholder="<spring:message code="createJourney.originUniversity.search" text="Type to search..."/>" />
                            <div id="universityDropdown" class="dropdown-menu" style="display: none;">
                                <c:forEach var="item" items="${universities}">
                                    <div class="dropdown-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                        <form:errors path="originUniversity" cssClass="error-message" />
                    </div>

                    <!-- Enhanced interests section with improved autocomplete and multi-select -->
                    <div class="form-group">
                        <form:label path="interests" cssClass="form-label">
                            <spring:message code="event.interest"/>
                        </form:label>

                        <!-- Hidden select that will hold the actual form data -->
                        <form:select path="interests" multiple="true" id="interestsSelect" style="display: none;">
                            <c:forEach var="item" items="${interests}">
                                <option value="${item.name}"><c:out value="${item.name}"/></option>
                            </c:forEach>
                        </form:select>

                        <!-- Custom UI for interests selection -->
                        <div class="autocomplete-wrapper">
                            <input type="text" id="interestSearch" class="autocomplete-input"
                                   placeholder="<spring:message code="event.interest.search" text="Search interests..."/>" />

                            <div id="interestDropdown" class="autocomplete-dropdown" style="display: none;">
                                <c:forEach var="item" items="${interests}">
                                    <div class="autocomplete-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- Selected interests will appear here as tags -->
                        <div id="selectedInterests" class="selected-tags"></div>

                        <form:errors path="interests" cssClass="error-message" />
                    </div>

                    <!-- Enhanced file upload area -->
                    <div class="form-group">
                        <form:label path="profilePicture" cssClass="form-label">
                            <spring:message code="createJourney.profile_picture"/>
                        </form:label>
                        <div class="file-upload">
                            <label class="file-upload-label">
                                <svg xmlns="http://www.w3.org/2000/svg" class="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                <span class="file-upload-text">
                                    <spring:message code="upload_picture.profile"/>
                                </span>
                                <span class="file-upload-hint">
                                    <spring:message code="upload_picture.hint" text="JPG or PNG, max 5MB"/>
                                </span>
                                <form:input path="profilePicture" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
                            </label>
                        </div>
                        <div id="filePreview" class="file-preview" style="display: none;">
                            <img id="previewImage" class="file-preview-image" src="#" alt="Preview" />
                            <span id="fileName" class="file-preview-name"></span>
                            <button type="button" id="removeFile" class="file-preview-remove" aria-label="Remove file">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                                </svg>
                            </button>
                        </div>
                        <form:errors path="profilePicture" cssClass="error-message" />
                    </div>
                </div>
            </div>

            <button type="submit" class="auth-button">
                <spring:message code="register.submit"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <spring:message code="register.have.account" text="Already have an account?"/>
            <a href="<c:url value='/login'/>" class="auth-link">
                <spring:message code="register.login" text="Sign in"/>
            </a>
        </div>
    </div>
</div>

<!-- Include the enhanced password strength script -->
<script>
    /**
     * Enhanced password strength meter and validation
     * Features:
     * - Visual password strength indicator
     * - Password visibility toggle
     * - Password confirmation validation
     * - Real-time feedback
     */
    document.addEventListener("DOMContentLoaded", () => {
        // Get DOM elements
        const passwordField = document.getElementById("password");
        const confirmPasswordField = document.getElementById("confirmPassword");
        const togglePasswordBtn = document.getElementById("togglePassword");
        const toggleConfirmPasswordBtn = document.getElementById("toggleConfirmPassword");

        // Password strength elements
        const passwordStrengthBar = document.getElementById("passwordStrengthBar");
        const passwordStrengthLabel = document.getElementById("passwordStrengthLabel");
        const passwordMessage = document.getElementById("passwordMessage");
        const passwordMatchMessage = document.getElementById("passwordMatchMessage");

        // Requirement elements
        const reqLength = document.getElementById("req-length");
        const reqLowercase = document.getElementById("req-lowercase");
        const reqUppercase = document.getElementById("req-uppercase");
        const reqNumber = document.getElementById("req-number");
        const reqSpecial = document.getElementById("req-special");

        // Get internationalized messages
        const messages = {
            veryWeak: document.getElementById("i18n-password-very-weak")?.value || "Very Weak",
            weak: document.getElementById("i18n-password-weak")?.value || "Weak",
            medium: document.getElementById("i18n-password-medium")?.value || "Medium",
            strong: document.getElementById("i18n-password-strong")?.value || "Strong",
            veryStrong: document.getElementById("i18n-password-very-strong")?.value || "Very Strong",
            strengthLabel: document.getElementById("i18n-password-strength")?.value || "Password Strength",
            addMoreStrength: document.getElementById("i18n-password-add-more")?.value || "Add more complexity",
            goodPassword: document.getElementById("i18n-password-good")?.value || "Good password",
            greatPassword: document.getElementById("i18n-password-great")?.value || "Great password",
            passwordsMatch: document.getElementById("i18n-password-match")?.value || "Passwords match",
            passwordsDontMatch: document.getElementById("i18n-password-mismatch")?.value || "Passwords do not match",
            showPassword: document.getElementById("i18n-password-show")?.value || "Show password",
            hidePassword: document.getElementById("i18n-password-hide")?.value || "Hide password",
            requirements: {
                length: document.getElementById("i18n-password-req-length")?.value || "8+ characters",
                lowercase: document.getElementById("i18n-password-req-lowercase")?.value || "Lowercase letter",
                uppercase: document.getElementById("i18n-password-req-uppercase")?.value || "Uppercase letter",
                number: document.getElementById("i18n-password-req-number")?.value || "Number",
                special: document.getElementById("i18n-password-req-special")?.value || "Special character",
            },
        };

        // Toggle password visibility
        if (togglePasswordBtn) {
            togglePasswordBtn.addEventListener("click", function () {
                const eyeIcon = document.getElementById("eyeIcon");
                const eyeSlashIcon = document.getElementById("eyeSlashIcon");

                const type = passwordField.getAttribute("type") === "password" ? "text" : "password";
                passwordField.setAttribute("type", type);

                // Toggle eye icons
                eyeIcon.style.display = type === "password" ? "inline" : "none";
                eyeSlashIcon.style.display = type === "password" ? "none" : "inline";

                // Update aria-label for accessibility
                this.setAttribute("aria-label", type === "password" ? messages.showPassword : messages.hidePassword);
            });
        }

        // Toggle confirm password visibility
        if (toggleConfirmPasswordBtn) {
            toggleConfirmPasswordBtn.addEventListener("click", function () {
                const confirmEyeIcon = document.getElementById("confirmEyeIcon");
                const confirmEyeSlashIcon = document.getElementById("confirmEyeSlashIcon");

                const type = confirmPasswordField.getAttribute("type") === "password" ? "text" : "password";
                confirmPasswordField.setAttribute("type", type);

                // Toggle eye icons
                confirmEyeIcon.style.display = type === "password" ? "inline" : "none";
                confirmEyeSlashIcon.style.display = type === "password" ? "none" : "inline";

                // Update aria-label for accessibility
                this.setAttribute("aria-label", type === "password" ? messages.showPassword : messages.hidePassword);
            });
        }

        // Evaluate password strength with detailed requirements
        function evaluatePasswordStrength(password) {
            if (!password) return { score: 0, requirements: {} };

            const requirements = {
                length: password.length >= 8,
                lowercase: /[a-z]/.test(password),
                uppercase: /[A-Z]/.test(password),
                number: /[0-9]/.test(password),
                special: /[^A-Za-z0-9]/.test(password),
            };

            // Calculate strength score (0-100)
            let score = 0;
            if (password.length > 0) {
                // Base score from requirements met (60%)
                const reqCount = Object.values(requirements).filter(Boolean).length;
                score = (reqCount / 5) * 60;

                // Additional score for length (40%)
                if (password.length >= 8) score += 10;
                if (password.length >= 10) score += 10;
                if (password.length >= 12) score += 10;
                if (password.length >= 14) score += 10;
            }

            return {
                score: Math.min(score, 100),
                requirements,
            };
        }

        // Update password strength UI with internationalized messages
        function updatePasswordStrengthUI(password) {
            if (!passwordStrengthBar) return;

            const { score, requirements } = evaluatePasswordStrength(password);

            // Update strength bar width
            passwordStrengthBar.style.width = score + "%";

            // Update UI based on score
            if (score === 0) {
                passwordStrengthBar.className = "password-bar";
                passwordStrengthLabel.textContent = "";
                passwordMessage.textContent = "";
            } else if (score < 25) {
                passwordStrengthBar.className = "password-bar strength-very-weak";
                passwordStrengthLabel.className = "password-label text-very-weak";
                passwordStrengthLabel.textContent = messages.veryWeak;
                passwordMessage.textContent = messages.addMoreStrength;
            } else if (score < 50) {
                passwordStrengthBar.className = "password-bar strength-weak";
                passwordStrengthLabel.className = "password-label text-weak";
                passwordStrengthLabel.textContent = messages.weak;
                passwordMessage.textContent = messages.addMoreStrength;
            } else if (score < 75) {
                passwordStrengthBar.className = "password-bar strength-medium";
                passwordStrengthLabel.className = "password-label text-medium";
                passwordStrengthLabel.textContent = messages.medium;
                passwordMessage.textContent = messages.goodPassword;
            } else if (score < 90) {
                passwordStrengthBar.className = "password-bar strength-strong";
                passwordStrengthLabel.className = "password-label text-strong";
                passwordStrengthLabel.textContent = messages.strong;
                passwordMessage.textContent = messages.goodPassword;
            } else {
                passwordStrengthBar.className = "password-bar strength-very-strong";
                passwordStrengthLabel.className = "password-label text-very-strong";
                passwordStrengthLabel.textContent = messages.veryStrong;
                passwordMessage.textContent = messages.greatPassword;
            }

            // Update requirement status
            if (reqLength) {
                reqLength.className = `requirement-item ${requirements.length ? "requirement-met" : "requirement-unmet"}`;
            }
            if (reqLowercase) {
                reqLowercase.className = `requirement-item ${requirements.lowercase ? "requirement-met" : "requirement-unmet"}`;
            }
            if (reqUppercase) {
                reqUppercase.className = `requirement-item ${requirements.uppercase ? "requirement-met" : "requirement-unmet"}`;
            }
            if (reqNumber) {
                reqNumber.className = `requirement-item ${requirements.number ? "requirement-met" : "requirement-unmet"}`;
            }
            if (reqSpecial) {
                reqSpecial.className = `requirement-item ${requirements.special ? "requirement-met" : "requirement-unmet"}`;
            }
        }

        // Check if passwords match
        function checkPasswordsMatch() {
            if (!confirmPasswordField || !passwordMatchMessage) return;

            const password = passwordField.value;
            const confirmPassword = confirmPasswordField.value;

            if (confirmPassword) {
                if (password === confirmPassword) {
                    passwordMatchMessage.textContent = messages.passwordsMatch;
                    passwordMatchMessage.className = "password-match-message match";
                    confirmPasswordField.classList.remove("error");
                } else {
                    passwordMatchMessage.textContent = messages.passwordsDontMatch;
                    passwordMatchMessage.className = "password-match-message mismatch";
                    confirmPasswordField.classList.add("error");
                }
            } else {
                passwordMatchMessage.textContent = "";
                confirmPasswordField.classList.remove("error");
            }
        }

        // Add event listeners
        if (passwordField) {
            passwordField.addEventListener("input", function () {
                updatePasswordStrengthUI(this.value);
                checkPasswordsMatch();
            });

            // Initialize on page load if password has a value
            if (passwordField.value) {
                updatePasswordStrengthUI(passwordField.value);
            }
        }

        if (confirmPasswordField) {
            confirmPasswordField.addEventListener("input", checkPasswordsMatch);

            // Initialize on page load if confirm password has a value
            if (confirmPasswordField.value) {
                checkPasswordsMatch();
            }
        }

        // Form validation
        const form = document.querySelector(".auth-form");
        if (form) {
            form.addEventListener("submit", (e) => {
                if (passwordField && confirmPasswordField) {
                    const password = passwordField.value;
                    const confirmPassword = confirmPasswordField.value;

                    if (password !== confirmPassword) {
                        e.preventDefault();
                        passwordMatchMessage.textContent = messages.passwordsDontMatch;
                        passwordMatchMessage.className = "password-match-message mismatch";
                        confirmPasswordField.classList.add("error");
                        confirmPasswordField.focus();
                    }
                }
            });
        }
    });
</script>

<!-- Enhanced autocomplete and interests selection script -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Initialize career autocomplete
        initAutocomplete("career", "careerSearch", "careerDropdown");

        // Initialize university autocomplete
        initAutocomplete("originUniversity", "universitySearch", "universityDropdown");

        // Initialize interests multi-select
        initInterestsMultiSelect();

        // Initialize file upload preview
        initFileUploadPreview();

        /**
         * Initialize autocomplete for select fields
         */
        function initAutocomplete(selectId, searchId, dropdownId) {
            const selectField = document.getElementById(selectId);
            const searchInput = document.getElementById(searchId);
            const dropdown = document.getElementById(dropdownId);

            if (!selectField || !searchInput || !dropdown) return;

            const dropdownItems = dropdown.querySelectorAll(".dropdown-item");

            // Show dropdown on focus
            searchInput.addEventListener("focus", function() {
                dropdown.style.display = "block";
                filterDropdownItems(this.value.toLowerCase(), dropdownItems);
            });

            // Hide dropdown when clicking outside
            document.addEventListener("click", function(e) {
                if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.style.display = "none";
                }
            });

            // Filter items as user types
            searchInput.addEventListener("input", function() {
                dropdown.style.display = "block";
                filterDropdownItems(this.value.toLowerCase(), dropdownItems);
            });

            // Handle item selection with visual feedback
            dropdownItems.forEach(item => {
                item.addEventListener("click", function() {
                    const value = this.dataset.value;
                    const text = this.textContent.trim();

                    // Update the select field
                    selectField.value = value;

                    // Update the search input
                    searchInput.value = text;

                    // Add highlight effect
                    searchInput.classList.add("highlight-selection");
                    setTimeout(() => {
                        searchInput.classList.remove("highlight-selection");
                    }, 1000);

                    // Hide dropdown
                    dropdown.style.display = "none";

                    // Trigger change event
                    const event = new Event("change");
                    selectField.dispatchEvent(event);
                });
            });

            // Initialize with selected value if any
            if (selectField.value) {
                const selectedOption = Array.from(selectField.options).find(option => option.value === selectField.value);
                if (selectedOption) {
                    searchInput.value = selectedOption.textContent;
                }
            }
        }

        /**
         * Filter dropdown items based on search text
         */
        function filterDropdownItems(searchText, items) {
            let visibleCount = 0;

            items.forEach(item => {
                const text = item.textContent.toLowerCase();
                const isVisible = text.includes(searchText);
                item.style.display = isVisible ? "block" : "none";
                if (isVisible) visibleCount++;
            });

            return visibleCount;
        }

        /**
         * Initialize interests multi-select with enhanced UX
         */
        function initInterestsMultiSelect() {
            const interestsSelect = document.getElementById("interestsSelect");
            const interestSearch = document.getElementById("interestSearch");
            const interestDropdown = document.getElementById("interestDropdown");
            const selectedInterests = document.getElementById("selectedInterests");

            if (!interestsSelect || !interestSearch || !interestDropdown || !selectedInterests) return;

            // Store selected values
            let selectedValues = [];

            // Initialize with any pre-selected values
            initializeSelectedValues();
            updateSelectedTags();

            // Show dropdown on focus
            interestSearch.addEventListener("focus", function() {
                interestDropdown.style.display = "block";
                filterOptions(this.value);
            });

            // Hide dropdown when clicking outside
            document.addEventListener("click", function(e) {
                if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
                    interestDropdown.style.display = "none";
                }
            });

            // Filter options as user types
            interestSearch.addEventListener("input", function() {
                interestDropdown.style.display = "block";
                filterOptions(this.value);
            });

            // Handle option selection with visual feedback
            const interestOptions = interestDropdown.querySelectorAll(".autocomplete-item");
            interestOptions.forEach(option => {
                option.addEventListener("click", function() {
                    const value = this.dataset.value;
                    const text = this.textContent.trim();

                    // Check if already selected
                    const exists = selectedValues.some(item => item.value === value);

                    if (!exists) {
                        // Add to selected values
                        selectedValues.push({ value, text });

                        // Update the hidden select
                        updateSelectElement();

                        // Highlight the selected option
                        this.classList.add("selected");
                        setTimeout(() => {
                            this.classList.remove("selected");
                        }, 500);
                    }

                    // Update the UI
                    updateSelectedTags();
                    interestSearch.value = "";
                    interestSearch.focus(); // Keep focus for adding more
                });
            });

            // Filter dropdown options based on search text
            function filterOptions(searchText) {
                const filter = searchText.toLowerCase();
                let visibleCount = 0;

                interestOptions.forEach(option => {
                    const text = option.textContent.toLowerCase();
                    const isVisible = text.includes(filter);
                    option.style.display = isVisible ? "block" : "none";
                    if (isVisible) visibleCount++;
                });

                // Show "no results" message if needed
                const noResultsMsg = document.getElementById("noInterestResults");
                if (visibleCount === 0) {
                    if (!noResultsMsg) {
                        const msg = document.createElement("div");
                        msg.id = "noInterestResults";
                        msg.className = "autocomplete-item no-results";
                        msg.textContent = "No matching interests found";
                        interestDropdown.appendChild(msg);
                    }
                } else if (noResultsMsg) {
                    noResultsMsg.remove();
                }
            }

            // Initialize selected values from the select element
            function initializeSelectedValues() {
                const options = interestsSelect.querySelectorAll("option");

                options.forEach(option => {
                    if (option.selected) {
                        selectedValues.push({
                            value: option.value,
                            text: option.textContent.trim()
                        });
                    }
                });
            }

            // Update the select element based on selectedValues array
            function updateSelectElement() {
                const options = interestsSelect.querySelectorAll("option");
                const selectedValueIds = selectedValues.map(item => item.value);

                options.forEach(option => {
                    option.selected = selectedValueIds.includes(option.value);
                });
            }

            // Update the selected tags UI
            function updateSelectedTags() {
                // Clear existing tags
                selectedInterests.innerHTML = "";

                if (selectedValues.length === 0) {
                    const emptyState = document.createElement("div");
                    emptyState.className = "empty-interests";
                    emptyState.textContent = "No interests selected";
                    selectedInterests.appendChild(emptyState);
                    return;
                }

                // Create tags for each selected value
                selectedValues.forEach(item => {
                    // Create tag container
                    const tag = document.createElement("div");
                    tag.className = "selected-tag";

                    // Add text node
                    const textNode = document.createTextNode(item.text);
                    tag.appendChild(textNode);

                    // Create remove button
                    const removeBtn = document.createElement("button");
                    removeBtn.type = "button";
                    removeBtn.className = "tag-remove";
                    removeBtn.dataset.value = item.value;
                    removeBtn.setAttribute("aria-label", `Remove ${item.text}`);

                    // Create SVG for the remove button
                    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
                    svg.setAttribute("width", "12");
                    svg.setAttribute("height", "12");
                    svg.setAttribute("fill", "currentColor");
                    svg.setAttribute("viewBox", "0 0 16 16");

                    const path = document.createElementNS("http://www.w3.org/2000/svg", "path");
                    path.setAttribute("d", "M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z");

                    svg.appendChild(path);
                    removeBtn.appendChild(svg);

                    // Add remove button functionality
                    removeBtn.addEventListener("click", function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        const valueToRemove = this.dataset.value;
                        selectedValues = selectedValues.filter(item => item.value !== valueToRemove);
                        updateSelectElement();
                        updateSelectedTags();
                        interestSearch.focus(); // Return focus to search input
                    });

                    // Add button to tag
                    tag.appendChild(removeBtn);

                    // Add tag to container
                    selectedInterests.appendChild(tag);
                });
            }
        }

        /**
         * Initialize file upload preview
         */
        function initFileUploadPreview() {
            const fileInput = document.querySelector('input[type="file"]');
            const filePreview = document.getElementById('filePreview');
            const previewImage = document.getElementById('previewImage');
            const fileName = document.getElementById('fileName');
            const removeFile = document.getElementById('removeFile');

            if (!fileInput || !filePreview || !previewImage || !fileName || !removeFile) return;

            fileInput.addEventListener('change', function() {
                if (this.files && this.files[0]) {
                    const file = this.files[0];

                    // Check file size (max 5MB)
                    if (file.size > 5 * 1024 * 1024) {
                        alert('File size exceeds 5MB limit');
                        this.value = '';
                        return;
                    }

                    // Update file name
                    fileName.textContent = file.name;

                    // Create preview image
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        previewImage.src = e.target.result;
                        filePreview.style.display = 'flex';
                    }
                    reader.readAsDataURL(file);
                }
            });

            // Remove file
            removeFile.addEventListener('click', function() {
                fileInput.value = '';
                filePreview.style.display = 'none';
                previewImage.src = '#';
                fileName.textContent = '';
            });
        }
    });
</script>
</body>
</html>
